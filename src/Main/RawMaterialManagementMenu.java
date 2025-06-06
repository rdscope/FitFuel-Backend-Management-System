package Main;

import model.RawMaterial;
import model.User;
import service.AuditLogServiceImpl;
import service.RawMaterialServiceImpl;
import service.ServiceException;

import java.math.BigDecimal;
import java.util.Scanner;

public class RawMaterialManagementMenu {
    public static void show(RawMaterialServiceImpl service, AuditLogServiceImpl auditLogService, User user, Scanner scanner) throws ServiceException {
        while (true) {
            System.out.println("\n------ Raw Material Management ------");
            System.out.println("1. View All Raw Materials");
            System.out.println("2. Add Raw Material");
            System.out.println("3. Delete Raw Material");
            System.out.println("4. Replenish Stock");
            System.out.println("5. Edit Raw Material Info (Name / Supplier / Threshold)");
            System.out.println("0. Back");
            System.out.print("CHOOSE...");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    service.getAll().forEach(r ->
                            System.out.println(r.getRawMaterialId() + " - " + r.getName() + " - Stock: " + r.getStockQuantity()));
                    auditLogService.log("RawMaterial", "READ", "Viewed all raw materials", user.getUserId());
                    break;
                case "2":
                    System.out.println("Enter Raw Material Name:");
                    String name = scanner.nextLine();

                    System.out.println("Enter Supplier:");
                    String supplier = scanner.nextLine();

                    System.out.println("Enter Initial Stock Quantity:");
                    BigDecimal stock = new BigDecimal(scanner.nextLine());

                    System.out.println("Enter Low Stock Threshold (e.g., 10.0):");
                    BigDecimal threshold = new BigDecimal(scanner.nextLine());

                    try {
                        RawMaterial rm = new RawMaterial();
                        rm.setName(name);
                        rm.setSupplier(supplier);
                        rm.setStockQuantity(stock);
                        rm.setLowStockThreshold(threshold);

                        service.insert(rm);
                        System.out.println("Raw Material Added with Low Stock Threshold.");
                        auditLogService.log("RawMaterial", "CREATE", "Added raw material: " + name, user.getUserId());
                    } catch (ServiceException e) {
                        System.out.println("Failed to Add: " + e.getMessage());
                    }
                    break;

                case "3":
                    System.out.println("Enter Raw Material ID to Delete:");
                    int delId = scanner.nextInt();
                    scanner.nextLine();
                    try {
                        service.deleteById(delId);
                        System.out.println("Deleted Successfully.");
                        auditLogService.log("RawMaterial", "DELETE", "Deleted raw material ID: " + delId, user.getUserId());
                    } catch (ServiceException e) {
                        System.out.println("Failed to Delete: " + e.getMessage());
                    }
                    break;
                case "4":
                    System.out.println("Enter Raw Material ID to Replenish:");
                    int rid = scanner.nextInt();
                    System.out.println("Enter Quantity to Add:");
//                    BigDecimal addQty = new BigDecimal(scanner.nextLine());
                    int i = scanner.nextInt();
                    scanner.nextLine(); // 清除換行符
                    BigDecimal addQty = BigDecimal.valueOf(i);
                    scanner.nextLine();
                    try {
                        service.replenishStock(rid, addQty);
                        System.out.println("Stock Replenished.");
                        auditLogService.log("RawMaterial", "UPDATE", "Replenished stock for material ID: " + rid + " by " + addQty, user.getUserId());
                    } catch (ServiceException e) {
                        System.out.println("Failed to Replenish: " + e.getMessage());
                    }
                    break;
                case "5":
                    System.out.println("Enter Raw Material ID to Edit:");
                    int editId = scanner.nextInt();
                    scanner.nextLine();

                    RawMaterial material = service.getById(editId);
                    if (material == null) {
                        System.out.println("Raw Material not found.");
                        break;
                    }

                    System.out.println("Current Name: " + material.getName());
                    System.out.println("Enter New Name (or press Enter to keep):");
                    String newName = scanner.nextLine();
                    if (!newName.isBlank()) {
                        material.setName(newName);
                    }

                    System.out.println("Current Supplier: " + material.getSupplier());
                    System.out.println("Enter New Supplier (or press Enter to keep):");
                    String newSupplier = scanner.nextLine();
                    if (!newSupplier.isBlank()) {
                        material.setSupplier(newSupplier);
                    }

                    System.out.println("Current Low Stock Threshold: " + material.getLowStockThreshold());
                    System.out.println("Enter New Threshold (or press Enter to keep):");
                    String newThresholdStr = scanner.nextLine();
                    if (!newThresholdStr.isBlank()) {
                        try {
                            BigDecimal newThreshold = new BigDecimal(newThresholdStr);
                            material.setLowStockThreshold(newThreshold);
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid threshold input. Skipped update.");
                        }
                    }

                    try {
                        service.update(material);
                        System.out.println("Raw Material updated.");
                        auditLogService.log("RawMaterial", "UPDATE", "Updated raw material ID: " + editId, user.getUserId());
                    } catch (ServiceException e) {
                        System.out.println("Failed to update: " + e.getMessage());
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
