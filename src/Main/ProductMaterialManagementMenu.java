package Main;

import model.ProductMaterial;
import model.RawMaterial;
import model.User;
import service.AuditLogServiceImpl;
import service.ProductMaterialServiceImpl;
import service.RawMaterialServiceImpl;
import service.ServiceException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class ProductMaterialManagementMenu {
    public static void show(ProductMaterialServiceImpl service, RawMaterialServiceImpl rawService, AuditLogServiceImpl auditLogService, User user, Scanner scanner) throws ServiceException {
        while (true) {
            System.out.println("\n----------- Product Materials Management -----------");
            System.out.println("1. View Product Materials By Product");
            System.out.println("2. Add Product Material Mapping");
            System.out.println("3. Edit Product Material Mapping");
            System.out.println("4. Delete Product Material Mapping");
            System.out.println("0. Back");
            System.out.print("CHOOSE...");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    System.out.println("Enter Product ID:");
                    int pid = scanner.nextInt();
                    scanner.nextLine();
                    List<ProductMaterial> list = service.findByProductId(pid);
                    if (list.isEmpty()) {
                        System.out.println("No materials mapped for this product.");
                    } else {
                        list.forEach(pm ->
                                System.out.println("Raw Material ID: " + pm.getRawMaterialId() +
                                        " - Quantity per Product: " + pm.getQuantityPerProduct()));
                    }
                    auditLogService.log("ProductMaterial", "READ", "Viewed raw materials for product ID: " + pid, user.getUserId());
                    break;
                case "2":
                    System.out.println("Enter Product ID:");
                    int newPid = scanner.nextInt();
                    scanner.nextLine();

                    System.out.println("Available Raw Materials:");
                    List<RawMaterial> rawList = rawService.getAll();
                    rawList.forEach(r ->
                            System.out.println(r.getRawMaterialId() + " - " + r.getName()));

                    while (true) {
                        System.out.println("Enter Raw Material ID (or 0 to finish):");
                        int rmId = scanner.nextInt();
                        scanner.nextLine();
                        if (rmId == 0) break;

                        System.out.println("Enter Quantity Needed per Product:");
                        BigDecimal qty = scanner.nextBigDecimal();
                        scanner.nextLine();

                        try {
                            service.addProductMaterialMapping(newPid, rmId, qty);
                            System.out.println("Mapping Added.");
                            auditLogService.log("ProductMaterial", "CREATE", "Added mapping: product " + newPid + ", material " + rmId + ", qty " + qty, user.getUserId());
                        } catch (Exception e) {
                            System.out.println("Failed to Add Mapping: " + e.getMessage());
                        }
                    }
                    break;
                case "3":
                    System.out.println("Enter Product ID to Edit Mapping:");
                    int editPid = scanner.nextInt();
                    scanner.nextLine();

                    System.out.println("Enter Raw Material ID:");
                    int editRmId = scanner.nextInt();
                    scanner.nextLine();

                    System.out.println("Enter New Quantity per Product:");
                    BigDecimal newQty = scanner.nextBigDecimal();
                    scanner.nextLine();

                    try {
                        service.updateProductMaterialMapping(editPid, editRmId, newQty);
                        System.out.println("Mapping Updated Successfully.");
                        auditLogService.log("ProductMaterial", "UPDATE", "Updated mapping: product " + editPid + ", material " + editRmId + ", new qty " + newQty, user.getUserId());
                    } catch (Exception e) {
                        System.out.println("Failed to Update Mapping: " + e.getMessage());
                    }
                    break;
                case "4":
                    System.out.println("Enter Product ID to Remove Mapping:");
                    int dpid = scanner.nextInt();
                    scanner.nextLine();

                    System.out.println("Enter Raw Material ID:");
                    int drmId = scanner.nextInt();
                    scanner.nextLine();
                    try {
                        service.deleteProductMaterialMapping(dpid, drmId);
                        System.out.println("Mapping Deleted.");
                        auditLogService.log("ProductMaterial", "DELETE", "Deleted mapping: product " + dpid + ", material " + drmId, user.getUserId());
                    } catch (Exception e) {
                        System.out.println("Failed to Delete Mapping: " + e.getMessage());
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