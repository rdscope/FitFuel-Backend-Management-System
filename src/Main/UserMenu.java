package Main;

import Util.DateUtil;
import dao.OrderDetailDaoImpl;
import model.*;
import service.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import Main.DiscountManagementMenu;
import static Main.ProductManagementMenu.mapCategoryIdToName;

public class UserMenu {
    public static void show(Connection conn, User user) throws ServiceException, SQLException {
        ProductServiceImpl productService = new ProductServiceImpl(conn);
        OrderServiceImpl orderService = new OrderServiceImpl(conn);
        ProductMaterialServiceImpl productMaterialService = new ProductMaterialServiceImpl(conn);
        Scanner scanner = new Scanner(System.in);
        AuditLogServiceImpl auditLogService = new AuditLogServiceImpl(conn);


        checkAndSendBirthdayDiscount(conn, user);
        checkAndSendThresholdDiscount(conn, user);
        showActivePromotions(conn);

        showAvailableDiscounts(conn, user);


        while (true) {
            System.out.println("\n========= USER MENU =========");
            System.out.println("1. Browse & Place Order");
            System.out.println("2. Add Items to Existing Unpaid Order");
            System.out.println("3. View My Orders (Include Unpaid & Paid)");
            System.out.println("4. Checkout (Unpaid Orders)");
            System.out.println("0. Log Out");
            System.out.print("CHOOSE...");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    browseAndPlaceOrder(scanner, productService, productMaterialService, orderService, user, auditLogService);
                    break;
                case "2":
                    addToExistingOrder(conn, scanner, productService, orderService, user, auditLogService);
                    break;
                case "3":
                    viewMyOrders(orderService, user, conn, auditLogService, scanner);
                    break;
                case "4":
                    checkoutUnpaidOrders(conn, scanner, orderService, user, auditLogService);
                    break;
                case "0":
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid Option");
            }
        }
    }

    private static void browseAndPlaceOrder(Scanner scanner,
                                            ProductServiceImpl productService,
                                            ProductMaterialServiceImpl productMaterialService,
                                            OrderServiceImpl orderService,
                                            User user,
                                            AuditLogServiceImpl auditLogService) throws ServiceException {
        Order order = startOrderWithCart(scanner, productService, productMaterialService, orderService, user);
        auditLogService.log("Order", "CREATE", "User created new order ID: " + order.getOrderId(), user.getUserId());
    }

    private static Order startOrderWithCart(Scanner scanner,
                                           ProductServiceImpl productService,
                                           ProductMaterialServiceImpl materialService,
                                           OrderServiceImpl orderService,
                                           User user) throws ServiceException {
        Order order = orderService.createOrder(user.getUserId());
        BigDecimal total = BigDecimal.ZERO;

        while (true) {
            ProductManagementMenu.viewProductsByCategoryFiltered(productService, materialService, false, true);

            System.out.println("\nEnter product ID to add (or 0 to checkout):");
            int pid = scanner.nextInt();
            if (pid == 0) break;

            Product p = productService.getById(pid);
            System.out.print("Enter quantity: ");
            int qty = scanner.nextInt();

            BigDecimal subtotal = p.getPrice().multiply(BigDecimal.valueOf(qty));
            total = total.add(subtotal);

            orderService.addOrderDetail(order.getOrderId(), pid, qty, p.getPrice(), "");
            System.out.println("Added " + p.getName() + " x" + qty + " to order.");
        }

        orderService.updateTotalAmount(order.getOrderId(), total);
        System.out.println("Order complete. Total = NT$" + total);
        return order;
    }


    private static void addToExistingOrder(Connection conn, Scanner scanner,
                                           ProductServiceImpl productService,
                                           OrderServiceImpl orderService,
                                           User user, AuditLogServiceImpl auditLogService) throws ServiceException {
        List<Order> unpaidOrders = orderService.findUnpaidByUser(user.getUserId());
        if (unpaidOrders.isEmpty()) {
            System.out.println("You have no unpaid orders to add items.");
            return;
        }

        System.out.println("Your Unpaid Orders:");
        unpaidOrders.forEach(o ->
                System.out.println("Order Code: " + o.getOrderCode()));

        System.out.print("Enter Order Code to Add Items: ");
        String orderCode = scanner.nextLine().trim();

        Order order = unpaidOrders.stream()
                .filter(o -> o.getOrderCode().equalsIgnoreCase(orderCode))
                .findFirst().orElse(null);

        if (order == null) {
            System.out.println("Order not found.");
            return;
        }

        while (true) {
            List<Product> products = productService.getAll();
            System.out.println("\n=== Products ===");
            for (Product p : products) {
                System.out.printf("%d. %s - NT$%s\n", p.getProductId(), p.getName(), p.getPrice());
            }

            System.out.print("Enter Product ID to Add (or 0 to finish): ");
            int productId = scanner.nextInt();
            scanner.nextLine();
            if (productId == 0) break;

            System.out.print("Enter Quantity: ");
            int quantity = scanner.nextInt();
            scanner.nextLine();

            try {
                orderService.addOrderItem(order.getOrderId(), user.getUserId(), productId, quantity);
                System.out.println("Item added to order.");
            } catch (ServiceException e) {
                System.out.println("Failed to add item: " + e.getMessage());
            }
        }

        System.out.println("Finished adding items to order.");
        auditLogService.log("Order", "UPDATE", "User added items to order: " + order.getOrderCode(), user.getUserId());
    }


    private static void viewMyOrders(OrderServiceImpl orderService, User user, Connection conn, AuditLogServiceImpl auditLogService, Scanner scanner) throws ServiceException, SQLException {
        List<Order> orders = orderService.findByUser(user.getUserId());
        if (orders.isEmpty()) {
            System.out.println("You have no orders yet.");
            return;
        }

        // 顯示所有訂單
        orders.forEach(o ->
                System.out.println("Order Code: " + o.getOrderCode() +
                        ", Total: NT$" + o.getTotalAmount() +
                        ", Status: " + o.getStatus()));

                System.out.print("Enter an Order Code to view its details (or 0 to skip): ");
        String input = scanner.nextLine().trim();

        if (input.equals("0")) {
            return;
        }

        if (!input.toUpperCase().startsWith("ORD-")) {
            System.out.println("Invalid format. Please enter a correct order code (e.g., ORD-xxxxx).\"");
            return;
        }

        // 查找該筆訂單是否屬於此使用者
        Order selectedOrder = orders.stream()
                .filter(o -> o.getOrderCode().equalsIgnoreCase(input))
                .findFirst()
                .orElse(null);

        if (selectedOrder == null) {
            System.out.println("Order not found. Please check your input.");
            return;
        }

        // 顯示該訂單的明細
        OrderDetailDaoImpl detailDao = new OrderDetailDaoImpl(conn);
        detailDao.printOrderDetailsByOrderId(selectedOrder.getOrderId());
        auditLogService.log("Order", "READ", "User viewed order details: " + selectedOrder.getOrderCode(), user.getUserId());
    }

    private static void checkoutUnpaidOrders(Connection conn, Scanner scanner, OrderServiceImpl orderService, User user, AuditLogServiceImpl auditLogService) throws ServiceException {
        var unpaidOrders = orderService.findUnpaidByUser(user.getUserId());
        if (unpaidOrders.isEmpty()) {
            System.out.println("You have no unpaid orders.");
            return;
        }

        System.out.println("\n=== Your Unpaid Orders (With Details) ===");

        ProductServiceImpl productService = new ProductServiceImpl(conn);

        for (Order o : unpaidOrders) {
            System.out.println("--------------------------------------------------");
            System.out.println("Order Code: " + o.getOrderCode());
            System.out.println("Total     : NT$" + o.getTotalAmount());
            System.out.println("Status    : " + o.getStatus());

            List<OrderDetail> details = orderService.findOrderDetailsByOrderId(o.getOrderId());
            for (OrderDetail d : details) {
                Product p = productService.getById(d.getProductId());
                System.out.printf(" - %s x%d @ NT$%s | Remark: %s\n",
                        p.getName(), d.getQuantity(), d.getPrice(), d.getRemark());
            }
        }

        System.out.println("--------------------------------------------------");
        System.out.print("\nEnter Order Code to Checkout (or 0 to cancel): ");
        String orderCode = scanner.nextLine().trim();
        if (orderCode.equals("0")) return;
        // 找出對應訂單
        var target = unpaidOrders.stream()
                .filter(o -> o.getOrderCode().equalsIgnoreCase(orderCode))
                .findFirst().orElse(null);

        if (target == null) {
            System.out.println("Order not found.");
            return;
        }

        int orderId = target.getOrderId();

        try {
            // Step 1: 折扣選擇與計算新總金額（含寫入 remark）
            boolean success = orderService.checkoutOrderWithDiscount(orderId, user.getUserId());
            if (!success) {
                System.out.println("Checkout aborted due to invalid or used discount.");
                return;
            }

            orderService.finalizeCheckout(orderId, orderCode, scanner, user.getUserId());
            auditLogService.log("Order", "UPDATE", "User checked out order: " + orderCode, user.getUserId());

        } catch (SQLException e) {
            System.out.println("Checkout failed due to database error.");
            e.printStackTrace();
        } catch (ServiceException e) {
            System.out.println("Checkout failed: " + e.getMessage());
        }
    }


    private static void checkAndSendBirthdayDiscount(Connection conn, User user) {
        try {
            UserDiscountServiceImpl userDiscountService = new UserDiscountServiceImpl(conn);
            DiscountServiceImpl discountService = new DiscountServiceImpl(conn);

            // Convert java.util.Date → java.sql.Date → LocalDate
            LocalDate birthday = new java.sql.Date(user.getBirthday().getTime()).toLocalDate();
            LocalDate today = LocalDate.now();

            // 取得今年的生日
            LocalDate birthdayThisYear = birthday.withYear(today.getYear());

            // 僅在「生日當天」才發送
            if (!today.getMonth().equals(birthdayThisYear.getMonth())) {
                return; // 非壽星月份，直接跳出
            }


            // 查詢固定的 Birthday 折扣模板
            Discount birthdayDiscount = discountService.getByName("Birthday 15% OFF"); // Match by name

            if (birthdayDiscount == null) {
                System.out.println("Birthday discount template not found in Discount table.");
                return;
            }

            // 檢查今年是否已發送生日折扣
            int thisYear = today.getYear();
            System.out.println("[DEBUG] checking birthday discount for user: " + user.getUserId());
            System.out.println("[DEBUG] checking discount_id: " + birthdayDiscount.getDiscountId());
            List<UserDiscount> allDiscounts = userDiscountService.findByUser(user.getUserId());
            allDiscounts.stream()
                    .filter(ud -> ud.getDiscountId() == birthdayDiscount.getDiscountId())
                    .forEach(ud -> System.out.println("[DEBUG] found one: startDate=" + ud.getStartDate() + ", status=" + ud.getStatus()));

            // 正式邏輯
            boolean alreadyIssuedThisYear = allDiscounts.stream()
                    .filter(ud -> ud.getDiscountId() == birthdayDiscount.getDiscountId())
                    .anyMatch(ud -> ud.getStartDate().toLocalDate().getYear() == thisYear);


            if (alreadyIssuedThisYear) {
                System.out.println("*** You already received a birthday discount this year.");
                return;
            }

            // 從今年生日當天起算兩個月
            LocalDate startDate = birthdayThisYear;
            LocalDate endDate = startDate.plusMonths(2);

            UserDiscount newCoupon = new UserDiscount(
                    user.getUserId(),
                    birthdayDiscount.getDiscountId(),
                    Date.valueOf(startDate),
                    Date.valueOf(endDate),
                    "Unused"
            );

            userDiscountService.insert(newCoupon);
            System.out.printf("*** A birthday discount coupon has been issued! Valid from %s to %s.%n", startDate, endDate);

        } catch (ServiceException e) {
            System.out.println("Failed to issue birthday discount: " + e.getMessage());
        }
    }

    private static void checkAndSendThresholdDiscount(Connection conn, User user) {
        try {
            DiscountServiceImpl discountService = new DiscountServiceImpl(conn);
            discountService.sendThresholdDiscountIfNeeded(user.getUserId());
        } catch (ServiceException e) {
            System.out.println("Failed to check threshold discount: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void showAvailableDiscounts(Connection conn, User user) {
        LocalDate today = LocalDate.now();

        try {
            UserDiscountServiceImpl uds = new UserDiscountServiceImpl(conn);
            DiscountServiceImpl ds = new DiscountServiceImpl(conn);
            List<UserDiscount> list = uds.findValidByUser(user.getUserId(), today);

            if (!list.isEmpty()) {
                System.out.println("Personal Available Discount Coupons:");
                for (UserDiscount ud : list) {
                    Discount d = ds.getById(ud.getDiscountId());
                    String type = d.isPercentage() ? "percentage" : "fixed";
                    String singleUse = d.isSingleUse() ? "Yes" : "No";
                    String needCode = d.getCode() != null ? "Yes" : "No";
                    System.out.println("- Name: " + d.getName() +
                            " ｜ Amount: " + d.getDiscountAmount() +
                            " ｜ Valid: " + ud.getStartDate() + " ~ " + ud.getEndDate() +
                            " ｜ Type: " + type +
                            " ｜ Single-Use: " + singleUse +
                            " ｜ Requires Code: " + needCode);
                }
            } else {
                System.out.println("No available discounts at the moment.");
            }
        } catch (ServiceException e) {
            System.out.println("Failed to load discount records: " + e.getMessage());
        }
    }

    private static void showActivePromotions(Connection conn) {
        try {
            DiscountServiceImpl discountService = new DiscountServiceImpl(conn);
            LocalDate today = LocalDate.now();

            List<Discount> active = discountService.getAll().stream()
                    .filter(d -> {
                        if (d.isRecurring()) {
                            LocalDate start = LocalDate.of(today.getYear(), d.getRecurringStartMonth(), d.getRecurringStartDay());
                            LocalDate end = LocalDate.of(today.getYear(), d.getRecurringEndMonth(), d.getRecurringEndDay());
                            return !today.isBefore(start) && !today.isAfter(end);
                        } else if (d.getStartDate() != null && d.getEndDate() != null) {
                            LocalDate start = d.getStartDate().toLocalDate();
                            LocalDate end = d.getEndDate().toLocalDate();
                            return !today.isBefore(start) && !today.isAfter(end);
                        }
                        return false;
                    })
                    .collect(Collectors.toList());

            if (!active.isEmpty()) {
                System.out.println("Available Promotions (Today):");
                for (Discount d : active) {
                    String type = d.isPercentage() ? "percentage" : "fixed";
                    String singleUse = d.isSingleUse() ? "Yes" : "No";
                    String tag = d.isRecurring() ? " ｜ [Annual]" : "";
                    String needCode = d.getCode() != null ? " ｜ Requires Code: Yes" : "";
                    String period = d.isRecurring()
                            ? LocalDate.of(today.getYear(), d.getRecurringStartMonth(), d.getRecurringStartDay()) +
                            " ~ " + LocalDate.of(today.getYear(), d.getRecurringEndMonth(), d.getRecurringEndDay())
                            : d.getStartDate() + " ~ " + d.getEndDate();

                    System.out.println("- Name: " + d.getName() +
                            " ｜ Amount: " + d.getDiscountAmount() +
                            " ｜ Period: " + period +
                            " ｜ Type: " + type +
                            " ｜ Single-Use: " + singleUse + tag + needCode);
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to load promotions: " + e.getMessage());
        }
    }
}

