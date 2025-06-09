package service;
// Too many No Usages
import Util.DiscountCodeUtil;
import dao.*;
import model.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static Util.DateUtil.isAnniversaryPeriod;

public class OrderServiceImpl implements SERVICE<Order> {
    private final ProductDao productDao;
    private final OrderDao orderDao;
    private final UserDao userDao;
    private final OrderDetailDao orderDetailDao;
    private final ProductMaterialDao productMaterialDao;
    private final RawMaterialDao rawMaterialDao;
    private final DiscountDao discountDao;
    private final UserDiscountDao userDiscountDao;
    private final DiscountServiceImpl discountService;
    private final UserDiscountServiceImpl userDiscountService;
    private final Connection conn;
    private Discount selectedDiscount;
    private String usedCode;
    private UserDiscount selectedUserCoupon;

    public Discount getSelectedDiscount() {
        return selectedDiscount;
    }

    public String getUsedCode() {
        return usedCode;
    }

    public UserDiscount getSelectedUserCoupon() {
        return selectedUserCoupon;
    }


    public OrderServiceImpl(Connection conn) {
        this.conn = conn;
        this.productDao = new ProductDaoImpl(conn);
        this.orderDao = new OrderDaoImpl(conn);
        this.userDao = new UserDaoImpl(conn);
        this.orderDetailDao = new OrderDetailDaoImpl(conn);
        this.productMaterialDao = new ProductMaterialDaoImpl(conn);
        this.rawMaterialDao = new RawMaterialDaoImpl(conn);
        this.discountDao = new DiscountDaoImpl(conn);
        this.userDiscountDao = new UserDiscountDaoImpl(conn);
        this.discountService = new DiscountServiceImpl(conn);
        this.userDiscountService = new UserDiscountServiceImpl(conn);
    }

    // 🔧 加入 categoryId 對應分類名稱
    private String mapCategoryIdToName(int categoryId) {
        switch (categoryId) {
            case 1: return CategoryConstants.PROTEIN_SHAKES;
            case 2: return CategoryConstants.FITNESS_MEAL_BOXES;
            case 3: return CategoryConstants.COLD_PRESSED_JUICES;
            case 4: return CategoryConstants.ENERGY_BARS;
            case 5: return CategoryConstants.VEGAN_MEALS;
            case 6: return CategoryConstants.LOW_CARB_OPTIONS;
            case 7: return CategoryConstants.LIMITED_EDITIONS;
            case 8: return CategoryConstants.SUPPLEMENTS;
            default: return "Unknown Category";
        }
    }
    // no usage on this


    @Override
    public boolean insert(Order order) throws ServiceException {
        try {
            return orderDao.insert(order);
        } catch (SQLException e) {
            throw new ServiceException("Failed Creating Order...", e);
        }
    }

    @Override
    public boolean update(Order order) throws ServiceException {
        try {
            return orderDao.update(order);
        } catch (SQLException e) {
            throw new ServiceException("Failed Updating Order...", e);
        }
    }

    @Override
    public boolean deleteById(int id) throws ServiceException {
        try {
            return orderDao.deleteById(id);
        } catch (SQLException e) {
            throw new ServiceException("Failed Deleting Order...", e);
        }
    }

    @Override
    public Order getById(int id) throws ServiceException {
        try {
            return orderDao.getById(id);
        } catch (SQLException e) {
            throw new ServiceException("Failed Searching Order...", e);
        }
    }

    @Override
    public List<Order> getAll() throws ServiceException {
        try {
            return orderDao.getAll();
        } catch (SQLException e) {
            throw new ServiceException("Failed Grabbing Orders...", e);
        }
    }

    public List<Order> findByUser(int userId) throws ServiceException {
        try {
            return orderDao.findByUser(userId);
        } catch (SQLException e) {
            throw new ServiceException("Failed Searching User's Order...", e);
        }
    }

    public List<Order> findByStatus(String status) throws ServiceException {
        try {
            return orderDao.findByStatus(status);
        } catch (SQLException e) {
            throw new ServiceException("Failed to retrieve orders by status", e);
        }
    }

    public List<Order> findUnpaidByUser(int userId) throws ServiceException {
        try {
            return orderDao.findUnpaidByUser(userId);
        } catch (SQLException e) {
            throw new ServiceException("Failed Searching Unpaid Orders...", e);
        }
    }

    public void checkoutOrder(String orderCode) throws ServiceException {
        try {
            Order order = orderDao.findByOrderCode(orderCode);
            if (order == null) {
                throw new ServiceException("Order not found: " + orderCode);
            }
            if ("isPaid".equalsIgnoreCase(order.getStatus())) {
                throw new ServiceException("Order already paid: " + orderCode);
            }

            // 更新訂單狀態為已付款
            if (!orderDao.updateOrderStatus(order.getOrderId(), "isPaid")) {
                throw new ServiceException("Failed to update order status.");
            }

            System.out.println("Order successfully checked out: " + orderCode);
        } catch (SQLException e) {
            throw new ServiceException("Failed to checkout by order code.", e);
        }
    }


    public List<OrderDetail> findOrderDetailsByOrderId(int orderId) throws ServiceException {
        try {
            return orderDetailDao.findByOrder(orderId);
        } catch (SQLException e) {
            throw new ServiceException("Failed to find order details", e);
        }
    }

    public List<Order> findByDateRange(Date start, Date end) throws ServiceException {
        try {
            return orderDao.findByDateRange(start, end);
        } catch (SQLException e) {
            throw new ServiceException("Failed to retrieve orders by date range", e);
        }
    }

    // Checkout from here, but it's large...
    // Step 1: 建立訂單（空 total）
    public Order createOrder(int userId) throws ServiceException {
        // TODO: 使用 generateOrderCode 產生 code
        // TODO: 呼叫 orderDao.insert(order)
        // TODO: 回傳 insert 後的最新一筆訂單

        // 1-1：檢查使用者資格與自動發送折扣
        //      透過 discountService：

        //          發送滿額優惠券（每滿 5000 元發 1 張）
        try {
            User user = userDao.getById(userId);
            java.sql.Date birthday = new java.sql.Date(user.getBirthday().getTime());
            discountService.sendThresholdDiscountIfNeeded(userId);

            String code = generateOrderCode(userId);
            Order order = new Order(0, code, userId, LocalDateTime.now(), "Unpaid", BigDecimal.ZERO);
            insert(order); // 呼叫你已經有的 insert()
            return orderDao.findLastCreatedByUser(userId); // 找最新訂單
        } catch (SQLException e) {
            throw new ServiceException("Failed to create order.", e);
        }
    }


    // Step 2: 加入一樣商品進訂單
    public void addOrderItem(int orderId, int userId, int productId, int quantity) throws ServiceException {
        // TODO: 查詢商品 / 驗庫存 or 原料
        // TODO: 若為現做商品需扣原料
        // TODO: 呼叫 addOrderDetail(orderId, productId, qty, price, "")

        // 2-1：查詢商品並檢查庫存 / 原料數量
        //      如果商品是成品，就檢查現有庫存是否足夠。
        //      如果商品是現做，就：
        //          檢查每種原料是否足夠
        //          扣除使用的原料庫存
        try {
            // 1. 查詢商品
            Product product = productDao.getById(productId);
            if (product == null) throw new ServiceException("Product not found.");

            // 2. 驗證庫存
            if (!product.isMadeToOrder()) {
                if (product.getStockQuantity() < quantity)
                    throw new ServiceException("Insufficient stock.");
                product.setStockQuantity(product.getStockQuantity() - quantity);
                if (!productDao.update(product))
                    throw new ServiceException("Failed to update stock.");
            } else {
                ProductMaterialServiceImpl pmService = new ProductMaterialServiceImpl(conn);
                int canMake = pmService.calculateCanMake(productId);
                if (canMake < quantity)
                    throw new ServiceException("Insufficient ingredients. Can only make " + canMake + " items.");

                // 扣除原料庫存
                List<ProductMaterial> materials = productMaterialDao.findByProductId(productId);
                for (ProductMaterial pm : materials) {
                    BigDecimal totalNeeded = pm.getQuantityPerProduct().multiply(BigDecimal.valueOf(quantity));
                    if (!rawMaterialDao.reduceStock(pm.getRawMaterialId(), totalNeeded))
                        throw new ServiceException("Failed to deduct material: " + pm.getRawMaterialId());
                }
            }

            // 3. 新增訂單明細
            addOrderDetail(orderId, productId, quantity, product.getPrice(), "");

            // 4. 更新訂單總金額
            BigDecimal newTotal = calculateOrderTotal(orderId);
            updateTotalAmount(orderId, newTotal);  // 使用 updateTotalAmount 來更新資料庫中的金額

        } catch (SQLException e) {
            throw new ServiceException("Error occurred while adding product to order.", e);
        }
    }


    // Step 3: 結帳與折扣選擇
    public boolean checkoutOrderWithDiscount(int orderId, int userId) throws ServiceException, SQLException {
        // 確保訂單總金額是最新的
        BigDecimal total = calculateOrderTotal(orderId);  // 使用已經存在的 calculateOrderTotal 方法來取得最新的總金額

        // 若訂單沒有項目，則拋出異常
        if (total.compareTo(BigDecimal.ZERO) == 0) {
            throw new ServiceException("Order has no items.");
        }

        // 取得訂單詳細資料
        List<OrderDetail> details = findOrderDetailsByOrderId(orderId);
        if (details.isEmpty()) throw new ServiceException("Order has no items.");


        Scanner scanner = new Scanner(System.in);
        String existingRemark = details.get(0).getRemark();

        if (existingRemark != null && !existingRemark.equalsIgnoreCase("None") && !existingRemark.isBlank()) {
            System.out.println("This order already has a discount applied: " + existingRemark);
            System.out.print("Do you want to change it? (Y/N): ");
            String answer = scanner.nextLine().trim();
            if (!answer.equalsIgnoreCase("Y")) {
                System.out.println("Keeping existing discount. Skipping re-selection.");
                selectedDiscount = null;
                usedCode = null;
                return false;
            }
        }

        // 取得折扣資訊
        LocalDate today = LocalDate.now();
        List<UserDiscount> personalCoupons = userDiscountService.findValidByUser(userId, today)
                .stream().filter(c -> !"Used".equalsIgnoreCase(c.getStatus())).toList();
        List<Discount> activeDiscounts = discountDao.findActiveDiscounts(Date.valueOf(today));

        // 初始化暫存狀態
        this.selectedDiscount = null;
        this.usedCode = null;
        this.selectedUserCoupon = null;
        String discountSourceLabel = "";

        int optionIndex = 1;
        Map<Integer, String> optionMap = new HashMap<>();

        System.out.println("=== Available Discount Options ===");

        for (UserDiscount ud : personalCoupons) {
            Discount d = discountDao.getById(ud.getDiscountId());
            String name = d.getName().toLowerCase();
            String tag = name.contains("birthday") ? "[Birthday]" :
                    name.contains("threshold") || name.contains("500") ? "[Threshold]" :
                            "[Personal]";
            System.out.printf("%d. Personal Coupon - %s %s (%s), valid until %s\n",
                    optionIndex, d.getName(), tag, d.getDiscountAmount(), ud.getEndDate());
            optionMap.put(optionIndex++, "user-" + ud.getId());
        }

        for (Discount d : activeDiscounts) {
            if (d.getCode() == null) {
                System.out.printf("%d. Campaign Discount - %s [Event] (%s)\n",
                        optionIndex, d.getName(), d.getDiscountAmount());
                optionMap.put(optionIndex++, "active-" + d.getDiscountId());
            }
        }

        System.out.printf("%d. Enter Discount Code\n", optionIndex);
        optionMap.put(optionIndex, "code");

        System.out.println("0. No Discount");
        System.out.print("Please choose a discount: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        String discountInfo = "None";

        if (choice == 0) {
            selectedDiscount = null;
            usedCode = null;
            return true;

        } else if (optionMap.get(choice).startsWith("user")) {
            int udId = Integer.parseInt(optionMap.get(choice).split("-")[1]);
            selectedUserCoupon = userDiscountService.getById(udId);

            if ("Used".equalsIgnoreCase(selectedUserCoupon.getStatus())) {
                System.out.println("You have already used this personal discount.");
                return false;
            }

            selectedDiscount = discountDao.getById(selectedUserCoupon.getDiscountId());

            String name = selectedDiscount.getName().toLowerCase();
            discountSourceLabel = name.contains("birthday") ? "[Birthday]" :
                    name.contains("threshold") || name.contains("500") ? "[Threshold]" :
                            "[Personal]";


        } else if (optionMap.get(choice).startsWith("active")) {
            int did = Integer.parseInt(optionMap.get(choice).split("-")[1]);
            Discount d = discountDao.getById(did);
            UsedDiscountCodeDao usedCodeDao = new UsedDiscountCodeDaoImpl(conn);

            if (d.isSingleUse() && usedCodeDao.hasUserUsedDiscountId(userId, did)) {
                System.out.println("You have already used this campaign discount.");
                return false;
            }
            selectedDiscount = d;
            discountSourceLabel = "[Event]";


        } else if (optionMap.get(choice).equals("code")) {
            System.out.print("Enter discount code: ");
            String codeInput = scanner.nextLine().trim();

            Discount match = discountDao.getAll().stream()
                    .filter(d -> d.getCode() != null && codeInput.equalsIgnoreCase(d.getCode()))
                    .findFirst().orElse(null);

            if (match == null) {
                System.out.println("Invalid discount code.");
                return false;
            }

            if (match.isSingleUse()) {
                UsedDiscountCodeDao usedCodeDao = new UsedDiscountCodeDaoImpl(conn);
                if (usedCodeDao.hasUserUsedCode(userId, codeInput)) {
                    System.out.println("This code has already been used.");
                    return false;
                }
            }

            selectedDiscount = match;
            usedCode = match.getCode();
            discountSourceLabel = "[Code]";
        }


        // 計算最終金額與顯示資訊
        if (selectedDiscount != null) {
            BigDecimal discountAmount;
            if (selectedDiscount.isPercentage()) {
                BigDecimal rate = selectedDiscount.getDiscountAmount();
                discountAmount = total.multiply(BigDecimal.ONE.subtract(rate));
                total = total.subtract(discountAmount);
            } else {
                discountAmount = selectedDiscount.getDiscountAmount();
                if (discountAmount.compareTo(total) > 0) {
                    discountAmount = total;
                }
                total = total.subtract(discountAmount);
            }

            discountInfo = selectedDiscount.getName() + " " + discountSourceLabel;
            System.out.println("Discount applied: " + discountInfo + ", New total: " + total);
        }

        updateTotalAmount(orderId, total);
        for (OrderDetail d : details) {
            d.setRemark(discountInfo);
            orderDetailDao.updateRemark(d.getOrderDetailId(), discountInfo);
        }

        return true;
    }

    public void finalizeCheckout(int orderId, String orderCode, Scanner scanner, int userId) throws ServiceException {
        PaymentMethod method = null;

        System.out.println("\n=== Checkout ===");
        System.out.println("Choose Payment Method:");
        System.out.println("1. CASH");
        System.out.println("2. PAYPAL");
        System.out.println("3. CREDIT CARD");
        System.out.println("4. LINE PAY");
        System.out.println("5. APPLE PAY");

        while (method == null) {
            System.out.print("Enter payment method number: ");
            String input = scanner.nextLine().trim();
            switch (input) {
                case "1" -> method = PaymentMethod.CASH;
                case "2" -> method = PaymentMethod.PAYPAL;
                case "3" -> method = PaymentMethod.CREDIT_CARD;
                case "4" -> method = PaymentMethod.LINE_PAY;
                case "5" -> method = PaymentMethod.APPLE_PAY;
                default -> System.out.println("Invalid choice. Please select a number from 1–5.");
            }
        }

        System.out.println("Selected Payment Method: " + method);

        try {
            if (!orderDao.updatePaymentMethod(orderId, method)) {
                throw new ServiceException("Failed to save payment method.");
            }

            // 最終確認付款後才寫入折扣使用紀錄
            if (selectedDiscount != null) {
                UsedDiscountCodeDao usedCodeDao = new UsedDiscountCodeDaoImpl(conn);

                if (selectedDiscount.isSingleUse() && selectedUserCoupon == null) {
                    boolean alreadyUsed = (usedCode != null)
                            ? usedCodeDao.hasUserUsedCode(userId, usedCode)
                            : usedCodeDao.hasUserUsedDiscountId(userId, selectedDiscount.getDiscountId());

                    if (alreadyUsed) {
                        System.out.println("You have already used this discount. Cannot proceed.");
                        return;
                    }
                }

                boolean insertSuccess = (usedCode != null)
                        ? usedCodeDao.insert(userId, usedCode, selectedDiscount.getDiscountId(), orderId)
                        : usedCodeDao.insert(userId, null, selectedDiscount.getDiscountId(), orderId);

                if (!insertSuccess) {
                    throw new ServiceException("Failed to record discount usage after checkout.");
                }
            }

            if (selectedUserCoupon != null) {
                if (!userDiscountService.updateStatus(selectedUserCoupon.getId(), "Used")) {
                    throw new ServiceException("Failed to update user discount status.");
                }
            }

            checkoutOrder(orderCode);
            System.out.println("Checkout completed. Order marked as paid.");

        } catch (SQLException e) {
            throw new ServiceException("Failed to update payment method.", e);
        }
    }



    private String generateOrderCode(int userId) throws SQLException {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int count = orderDao.countOrdersByUserAndDate(userId, LocalDate.now());
        return String.format("ORD-u%03d-%s-%03d", userId, today, count + 1);
    }

    public void addOrderDetail(int orderId, int productId, int quantity, BigDecimal unitPrice, String remark)
            throws ServiceException {
        try {
            OrderDetail detail = new OrderDetail(0, orderId, productId, quantity, unitPrice);
            detail.setRemark(remark);
            if (!orderDetailDao.insert(detail)) {
                throw new ServiceException("Failed to insert order detail.");
            }
        } catch (SQLException e) {
            throw new ServiceException("Failed to add order detail.", e);
        }
    }


    private BigDecimal calculateOrderTotal(int orderId) throws ServiceException {
        try {
            // 查詢訂單明細
            List<OrderDetail> orderDetails = orderDetailDao.findByOrder(orderId);
            BigDecimal total = BigDecimal.ZERO;

            // 計算所有商品的總價
            for (OrderDetail detail : orderDetails) {
                total = total.add(detail.getPrice().multiply(BigDecimal.valueOf(detail.getQuantity())));
            }

            return total;
        } catch (SQLException e) {
            throw new ServiceException("Error occurred while calculating order total price.", e);
        }
    }

    public void updateTotalAmount(int orderId, BigDecimal newTotal) throws ServiceException {
        try {
            if (!orderDao.updateOrderTotal(orderId, newTotal)) {
                throw new ServiceException("Failed to update order total.");
            }
        } catch (SQLException e) {
            throw new ServiceException("Failed to update order total.", e);
        }
    }
}
