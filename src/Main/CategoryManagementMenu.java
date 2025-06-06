package Main;

import model.Category;
import model.User;
import service.AuditLogServiceImpl;
import service.CategoryServiceImpl;
import service.ServiceException;

import java.util.Scanner;

public class CategoryManagementMenu {
    public static void show(CategoryServiceImpl service, AuditLogServiceImpl auditLogService, User user, Scanner scanner) throws ServiceException {
        while(true){
            System.out.println("\n----------- Categories Management -----------"); // 產品原料管理
            System.out.println("1. View All Categories");
            System.out.println("2. Add Category");
            System.out.println("3. Edit Category");
            System.out.println("4. Delete Category");
            System.out.println("0. Back");
            System.out.print("CHOOSE...");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    service.getAll().forEach(c ->
                            System.out.println(c.getCategoryId() + " - " + c.getCategoryName()));
                            auditLogService.log("Category", "READ", "Viewed all categories", user.getUserId());
                    break;
                case "2":
                    System.out.println("Enter Category Name:");
                    String name = scanner.nextLine();
                    try {
                        service.insert(new Category(0, name));
                        System.out.println("Category Added.");
                        auditLogService.log("Category", "CREATE", "Added new category: " + name, user.getUserId());
                    } catch (Exception e) {
                        System.out.println("Failed to Add Category: " + e.getMessage());
                    }
                    break;
                case "3":
                    System.out.println("Enter Category ID to Edit:");
                    int editId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Enter New Name:");
                    String newName = scanner.nextLine();
                    try {
                        Category c = service.getById(editId);
                        String oldName = c.getCategoryName();
                        c.setCategoryName(newName);
                        service.update(c);
                        System.out.println("Category Updated.");
                        auditLogService.log("Category", "UPDATE", "Edited category ID " + editId + ": " + oldName + " → " + newName, user.getUserId());
                    } catch (Exception e) {
                        System.out.println("Failed to Edit Category: " + e.getMessage());
                    }
                    break;
                case "4":
                    System.out.println("Enter Category ID to Delete:");
                    int delId = scanner.nextInt();
                    scanner.nextLine();
                    try {
                        service.deleteById(delId);
                        System.out.println("Category Deleted.");
                        auditLogService.log("Category", "DELETE", "Deleted category ID: " + delId, user.getUserId());
                    } catch (Exception e) {
                        System.out.println("Failed to Delete Category: " + e.getMessage());
                    }
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid Option");
            }
        }
    }
}