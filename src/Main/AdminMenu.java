package Main;

import dao.OrderDetailDaoImpl;
import model.*;
import service.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;


public class AdminMenu {
    public static void show(Connection conn, User user) throws ServiceException, SQLException {
        Scanner scanner = new Scanner(System.in);

        ProductServiceImpl productService = new ProductServiceImpl(conn);
        RawMaterialServiceImpl rawMaterialService = new RawMaterialServiceImpl(conn);
        ProductMaterialServiceImpl productMaterialService = new ProductMaterialServiceImpl(conn);
        CategoryServiceImpl categoryService = new CategoryServiceImpl(conn);
        OrderServiceImpl orderService = new OrderServiceImpl(conn);
        DiscountServiceImpl discountService = new DiscountServiceImpl(conn);
//        UserDiscountServiceImpl userDiscountService = new UserDiscountServiceImpl(conn);
        UserServiceImpl userService = new UserServiceImpl(conn);
        AuditLogServiceImpl auditLogService = new AuditLogServiceImpl(conn);

        while (true) {
            System.out.println("\n========= ADMINISTRATOR MENU =========");
            System.out.println("1_ Manage Products");
            System.out.println("2_ Manage Raw Materials");
            System.out.println("3_ Manage Product Materials");
            System.out.println("4_ Manage Categories");
//            System.out.println("5_ View Products");
            System.out.println("5_ Manage Orders");
            System.out.println("6. Place Order for User");

            System.out.println("7_ Discount Management");

            System.out.println("8_ View Finished Products Low Stock");
            System.out.println("9_ View Made-to-Order Raw Material Shortage");

            System.out.println("0_ Log Out");
            System.out.print("CHOOSE...");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    auditLogService.log("Product", "READ", "Admin accessed Product Management Menu", user.getUserId());
                    ProductManagementMenu.show(productService, productMaterialService, auditLogService,user, scanner);
                    break;
                case "2":
                    auditLogService.log("RawMaterial", "READ", "Admin accessed Raw Material Management Menu", user.getUserId());
                    RawMaterialManagementMenu.show(rawMaterialService, auditLogService, user, scanner);
                    break;
                case "3":
                    auditLogService.log("ProductMaterial", "READ", "Admin accessed Product Material Mapping Menu", user.getUserId());
                    ProductMaterialManagementMenu.show(productMaterialService, rawMaterialService, auditLogService, user, scanner);
                    break;
                case "4":
                    auditLogService.log("Category", "READ", "Admin accessed Category Management Menu", user.getUserId());
                    CategoryManagementMenu.show(categoryService, auditLogService, user, scanner);
                    break;
//                case "5":
//                    try {
//                        List<Product> allProducts = productService.getAll();
//                        for (Product p : allProducts) {
//                            if (p.isMadeToOrder()) {
//                                int canMake = productMaterialService.calculateCanMake(p.getProductId());
//                                System.out.printf("%d - %s (Category: %d) - NT$%.2f - Stock: 0 - Made To Order: true - Can Make: %d units\n",
//                                        p.getProductId(), p.getName(), p.getCategoryId(), p.getPrice(), canMake);
//                            } else {
//                                System.out.printf("%d - %s (Category: %d) - NT$%.2f - Stock: %d - Made To Order: false\n",
//                                        p.getProductId(), p.getName(), p.getCategoryId(), p.getPrice(), p.getStockQuantity());
//                            }
//                        }
//                    } catch (ServiceException e) {
//                        System.out.println("Failed to retrieve product list: " + e.getMessage());
//                    }
//                    break;
                case "5":
                    auditLogService.log("Order", "READ", "Admin accessed Order Management Menu", user.getUserId());
                    OrderManagementMenu.show(conn, orderService, userService, auditLogService, user, scanner);
                    break;


                case "6":
                    auditLogService.log("Order", "CREATE", "Admin placed an order for another user", user.getUserId());
                    adminPlaceOrderForUser(conn, scanner);
                    break;


                case "7":
                    auditLogService.log("Discount", "READ", "Admin accessed Discount Management Menu", user.getUserId());
                    DiscountManagementMenu.show(conn, scanner, discountService, auditLogService, user);
                    break;

                // Connect SQL to check if it works fine... # 8 ~ 9
                case "8":  // 成品商品警戒值檢查
                    auditLogService.log("Product", "READ", "Admin checked finished product low stock", user.getUserId());
                    System.out.print("Enter stock threshold for finished products (integer): ");
                    int threshold = scanner.nextInt();
                    scanner.nextLine();
                    try {
                        List<Product> allProducts = productService.getAll();
                        boolean hasLowStock = false;
                        for (Product p : allProducts) {
                            if (!p.isMadeToOrder() && p.getStockQuantity() < threshold) {
                                System.out.println("Low stock for finished product - ID: " + p.getProductId() +
                                        ", Name: " + p.getName() +
                                        ", Stock: " + p.getStockQuantity());
                                hasLowStock = true;
                            }
                        }
                        if (!hasLowStock) {
                            System.out.println("All finished products are sufficiently stocked.");
                        }
                    } catch (ServiceException e) {
                        System.out.println("Failed to retrieve products: " + e.getMessage());
                    }
                    break;

                case "9":  // 現做商品原料是否低於各自警戒值
                    auditLogService.log("RawMaterial", "READ", "Admin checked made-to-order raw material shortage", user.getUserId());
                    try {
                        List<Product> madeToOrderProducts = productService.findMadeToOrderProducts();
                        boolean anyShortage = false;

                        for (Product p : madeToOrderProducts) {
                            List<ProductMaterial> materials = productMaterialService.findByProductId(p.getProductId());
                            for (ProductMaterial pm : materials) {
                                RawMaterial rm = rawMaterialService.getById(pm.getRawMaterialId());
                                if (rm.getStockQuantity().compareTo(rm.getLowStockThreshold()) < 0) {
                                    System.out.println("Made-to-order product \"" +
                                            p.getName() + "\" requires raw material \"" +
                                            rm.getName() + "\" which is below threshold: Current = " +
                                            rm.getStockQuantity() + ", Threshold = " +
                                            rm.getLowStockThreshold());
                                    anyShortage = true;
                                }
                            }
                        }

                        if (!anyShortage) {
                            System.out.println("All raw materials for made-to-order products are above threshold.");
                        }
                    } catch (ServiceException e) {
                        System.out.println("Failed to check raw materials: " + e.getMessage());
                    }
                    break;


                case "0":
                    System.out.println("Logging out...");
                    return;

                default:
                    System.out.println("Invalid Option");
            }
        }
    }

    public static void adminPlaceOrderForUser(Connection conn, Scanner scanner) throws ServiceException, SQLException {
        UserServiceImpl userService = new UserServiceImpl(conn);
        ProductServiceImpl productService = new ProductServiceImpl(conn);
        ProductMaterialServiceImpl materialService = new ProductMaterialServiceImpl(conn);
        OrderServiceImpl orderService = new OrderServiceImpl(conn);

        int inputUserId;
        User user;

        // Step 1: 取得使用者或 default user
        while (true) {
            try {
                System.out.print("Enter user ID (100 for default user): ");
                inputUserId = Integer.parseInt(scanner.nextLine().trim());
                user = (inputUserId == 0) ? userService.getById(1) : userService.getById(inputUserId);
                if (user == null) {
                    System.out.println("User not found. Using default user.");
                    user = userService.getById(1);
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer user ID.");
            }
        }

        Order order = orderService.createOrder(user.getUserId());
        System.out.println("Placing order for: " + user.getName() + " (User ID: " + user.getUserId() + ")");
        BigDecimal total = BigDecimal.ZERO;

        // Step 2: 商品加入迴圈
        while (true) {
            List<Product> products = productService.getAll();
            System.out.println("\n=== Product List ===");
            for (Product p : products) {
                System.out.printf("%d. (%s) %s - NT$%s\n",
                        p.getProductId(),
                        p.isMadeToOrder() ? "Made-to-Order" : "In Stock",
                        p.getName(),
                        p.getPrice());
            }

            int productId;
            try {
                System.out.print("Enter Product ID to add (or 0 to finish): ");
                productId = Integer.parseInt(scanner.nextLine().trim());
                if (productId == 0) break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid product ID.");
                continue;
            }

            Product product = productService.getById(productId);
            if (product == null) {
                System.out.println("Product not found.");
                continue;
            }

            int quantity;
            try {
                System.out.print("Enter Quantity: ");
                quantity = Integer.parseInt(scanner.nextLine().trim());
                if (quantity <= 0) {
                    System.out.println("Quantity must be positive.");
                    continue;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid quantity.");
                continue;
            }

            orderService.addOrderItem(order.getOrderId(), user.getUserId(), productId, quantity);
            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(quantity));
            total = total.add(subtotal);
            System.out.printf("Added %s x%d. Subtotal: NT$%s\n", product.getName(), quantity, subtotal);
        }

        // Step 3: 更新總金額
        orderService.updateTotalAmount(order.getOrderId(), total);
        System.out.println("Order created. Total: NT$" + total);

        // Step 4: 結帳（使用共用 finalizeCheckout 方法）
        orderService.finalizeCheckout(order.getOrderId(), order.getOrderCode(), scanner, user.getUserId());
    }


}
