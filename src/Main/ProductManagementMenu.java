package Main;

import model.Product;
import model.User;
import service.AuditLogServiceImpl;
import service.ProductMaterialServiceImpl;
import service.ProductServiceImpl;
import service.ServiceException;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ProductManagementMenu {

    public static void show(ProductServiceImpl productService, ProductMaterialServiceImpl productMaterialService, AuditLogServiceImpl auditLogService, User user, Scanner scanner) throws ServiceException {
        while (true) {
            System.out.println("\n----------- Product Management -----------");
            System.out.println("1. View All Products");
            System.out.println("2. View Products Grouped by Category");
            System.out.println("3. View All Made-to-Order Products");
            System.out.println("4. Add Product");
            System.out.println("5. Delete Product");
            System.out.println("6. Edit Product");
            System.out.println("7. Restock Product");
            System.out.println("0. Back");
            System.out.print("CHOOSE...");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    productService.getAll().forEach(p -> {
                        System.out.print(p.getProductId() + " - " + p.getName() +
                                " (Category: " + p.getCategoryId() + ") - NT$" + p.getPrice() +
                                " - Stock: " + p.getStockQuantity() +
                                " - Made To Order: " + p.isMadeToOrder());

                        if (p.isMadeToOrder()) {
                            try {
                                int max = productService.calculateMaxProduction(productService.getConnection(), p.getProductId());
                                System.out.print(" - Can Make: " + max + " units");
                            } catch (ServiceException e) {
                                System.out.print(" - Can Make: [error]");
                            }
                        }
                        System.out.println();
                    });
                    auditLogService.log("Product", "READ", "Viewed all products", user.getUserId());
                    break;

                case "2":
                    viewProductsByCategory(productService, productMaterialService);
                    auditLogService.log("Product", "READ", "Viewed products grouped by category", user.getUserId());
                    break;

                case "3":
                    viewMadeToOrderByCategory(productService, productMaterialService);
                    auditLogService.log("Product", "READ", "Viewed made-to-order products grouped by category", user.getUserId());
                    break;

                case "4":
                    // scanner.nextLine(); // 清掉之前的換行（避免 nextLine 被跳過）

                    System.out.println("Enter Product Name:");
                    String name = scanner.nextLine();

                    System.out.println("Enter Category ID:");
                    int categoryId = scanner.nextInt();
                    scanner.nextLine(); // 清除換行

                    System.out.println("Enter Product Price:");
                    BigDecimal price = scanner.nextBigDecimal();
                    scanner.nextLine(); // 清除換行

                    System.out.println("Is Made To Order? (true/false):");
                    boolean isMadeToOrder = scanner.nextBoolean();
                    scanner.nextLine(); // 清除換行

                    int stockQty = 0;
                    if (!isMadeToOrder) {
                        System.out.println("Enter Stock Quantity:");
                        stockQty = scanner.nextInt();
                        scanner.nextLine(); // 清除換行
                    } else {
                        System.out.println("This product is made-to-order. Please remember to bind its materials in ProductMaterial.");

                    }

                    try {
                        boolean success = productService.createProduct(name, categoryId, price, isMadeToOrder, stockQty);
                        if (success) {
                            System.out.println("Product created successfully.");
                            auditLogService.log("Product", "CREATE", "Created new product: " + name, user.getUserId());
                        } else {
                            System.out.println("Failed to create product.");
                        }
                    } catch (ServiceException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;


                case "5":
                    System.out.println("Enter Product ID to Delete:");
                    int delId = scanner.nextInt();
                    scanner.nextLine();
                    try {
                        productService.deleteById(delId);
                        System.out.println("Product Deleted Successfully.");
                        auditLogService.log("Product", "DELETE", "Deleted product ID: " + delId, user.getUserId());
                    } catch (Exception e) {
                        System.out.println("Failed to Delete Product: " + e.getMessage());
                    }
                    break;


                case "6":
                    System.out.println("Enter Product ID to Edit:");
                    int editId = scanner.nextInt();
                    scanner.nextLine(); // 清除換行

                    try {
                        Product product = productService.getById(editId);

                        System.out.println("Enter New Name (Current: " + product.getName() + "):");
                        String newName = scanner.nextLine();

                        System.out.println("Enter New Category ID (Current: " + product.getCategoryId() + "):");
                        int newCategoryId = scanner.nextInt();
                        scanner.nextLine();

                        System.out.println("Enter New Price (Current: " + product.getPrice() + "):");
                        BigDecimal newPrice = scanner.nextBigDecimal();
                        scanner.nextLine();

                        System.out.println("Is Made To Order? (Current: " + product.isMadeToOrder() + "):");
                        boolean newIsMadeToOrder = scanner.nextBoolean();
                        scanner.nextLine();

                        int newStockQty = 0;
                        if (!newIsMadeToOrder) {
                            System.out.println("Enter New Stock Quantity (Current: " + product.getStockQuantity() + "):");
                            newStockQty = scanner.nextInt();
                            scanner.nextLine();
                        } else {
                            System.out.println("This product is made-to-order. Stock will be set to 0.");
                        }

                        // 設定新的屬性
                        product.setName(newName);
                        product.setCategoryId(newCategoryId);
                        product.setPrice(newPrice);
                        product.setMadeToOrder(newIsMadeToOrder);
                        product.setStockQuantity(newIsMadeToOrder ? 0 : newStockQty);

                        productService.update(product);
                        System.out.println("Product Updated Successfully.");
                        auditLogService.log("Product", "UPDATE", "Edited product ID: " + editId, user.getUserId());
                    } catch (Exception e) {
                        System.out.println("Failed to Edit Product: " + e.getMessage());
                    }
                    break;


                case "7":
                    System.out.println("Enter Product ID to restock:");
                    int prodId = scanner.nextInt();
                    scanner.nextLine();

                    try {
                        Product product = productService.getById(prodId);
                        System.out.println("You selected: " + product.getName() +
                                (product.isMadeToOrder() ? " [Made To Order]" : " [Finished Product]"));

                        if (product.isMadeToOrder()) {
                            System.out.println("This product is made-to-order and does not use stock. Restocking is not applicable.");
                            return;
                        }

                        System.out.println("Current stock: " + product.getStockQuantity());
                        System.out.println("Enter amount to restock:");
                        int qty = scanner.nextInt();
                        scanner.nextLine();

                        int newStock = product.getStockQuantity() + qty;
                        product.setStockQuantity(newStock);
                        productService.update(product);

                        System.out.println("Stock updated to: " + newStock);
                        auditLogService.log("Product", "UPDATE", "Restocked product ID: " + prodId + " by " + qty, user.getUserId());
                    } catch (Exception e) {
                        System.out.println("Failed to restock: " + e.getMessage());
                    }


                case "0":
                    return;
                default:
                    System.out.println("Invalid Option");
            }
        }
    }

    public static void viewProductsByCategoryFiltered(ProductServiceImpl productService,
                                                      ProductMaterialServiceImpl productMaterialService,
                                                      boolean onlyMadeToOrder,
                                                      boolean simplified) {
        try {
            List<Product> products = productService.getAll();

            // 若只看 Made To Order，就先過濾
            if (onlyMadeToOrder) {
                products = products.stream()
                        .filter(Product::isMadeToOrder)
                        .collect(Collectors.toList());
            }

            Map<Integer, List<Product>> grouped = products.stream()
                    .collect(Collectors.groupingBy(Product::getCategoryId));

            for (Map.Entry<Integer, List<Product>> entry : grouped.entrySet()) {
                int categoryId = entry.getKey();
                String categoryName = mapCategoryIdToName(categoryId);
                System.out.println("\n===== [" + categoryName + "] =====");

                for (Product p : entry.getValue()) {
                    if (simplified) {
                        System.out.printf("%d. %s - NT$%.2f\n", p.getProductId(), p.getName(), p.getPrice());
                    } else {
                        if (p.isMadeToOrder()) {
                            int canMake = productMaterialService.calculateCanMake(p.getProductId());
                            System.out.printf("%d - %s - NT$%.2f - Made To Order: true - Can Make: %d units\n",
                                    p.getProductId(), p.getName(), p.getPrice(), canMake);
                        } else {
                            System.out.printf("%d - %s - NT$%.2f - Stock: %d - Made To Order: false\n",
                                    p.getProductId(), p.getName(), p.getPrice(), p.getStockQuantity());
                        }
                    }
                }
            }

        } catch (ServiceException e) {
            System.out.println("Failed to display products by category: " + e.getMessage());
        }
    }

    public static void viewProductsByCategory(ProductServiceImpl productService,ProductMaterialServiceImpl productMaterialService) {
        viewProductsByCategoryFiltered(productService, productMaterialService, false, false);
    }

    public static void viewMadeToOrderByCategory(ProductServiceImpl productService,ProductMaterialServiceImpl productMaterialService) {
        viewProductsByCategoryFiltered(productService, productMaterialService, true, false);
    }

    public static String mapCategoryIdToName(int categoryId) {
        switch (categoryId) {
            case 1: return "Protein Shakes";
            case 2: return "Fitness Meal Boxes";
            case 3: return "Cold-Pressed Juices";
            case 4: return "Energy Bars";
            case 5: return "Vegan Meals";
            case 6: return "Low-Carb Options";
            case 7: return "Limited Editions";
            case 8: return "Supplements";
            default: return "Other";
        }
    }

}
