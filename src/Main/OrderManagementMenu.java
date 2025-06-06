package Main;

import dao.OrderDetailDaoImpl;
import model.Order;
import model.OrderStatus;
import model.User;
import service.AuditLogServiceImpl;
import service.OrderServiceImpl;
import service.ServiceException;
import service.UserServiceImpl;

import java.sql.Connection;
import java.sql.Date;
import java.util.List;
import java.util.Scanner;

public class OrderManagementMenu {
    public static void show(Connection conn, OrderServiceImpl orderService, UserServiceImpl userService, AuditLogServiceImpl auditLogService, User user, Scanner scanner) throws ServiceException {
        while(true){
            System.out.println("\n----------- Order Overview -----------"); // 產品原料管理
            System.out.println("1. View All Orders");
            System.out.println("2. View Orders By Status");
            System.out.println("3. View Orders By Date Range");
//            System.out.println("4. View Orders' Details By ID"); --unfulfilled
            System.out.println("4. View Orders' Details By OrderID (with Remarks)");
            System.out.println("0. Back");
            System.out.print("CHOOSE...");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    orderService.getAll().forEach(o ->
                            System.out.println("Order ID: " + o.getOrderId() +
                                    ", User ID: " + o.getUserId() +
                                    ", Status: " + o.getStatus() +
                                    ", Total: NT$" + o.getTotalAmount() +
                                    ", Date: " + o.getOrderDate()));
                    auditLogService.log("Order", "READ", "Viewed all orders", user.getUserId());
                    break;
                case "2":
                    String statusInput = "";
                    while (true) {
                        System.out.println("Enter Status (isPaid / unPaid):");
                        statusInput = scanner.nextLine().trim();

                        if (OrderStatus.isValid(statusInput)) {
                            break; // 合法輸入
                        } else {
                            System.out.println("Invalid input. Please enter only 'isPaid' or 'unPaid'.");
                        }
                    }

                    // 查詢並顯示訂單
                    List<Order> orders = orderService.findByStatus(statusInput);
                    if (orders.isEmpty()) {
                        System.out.println("No orders found with status: " + statusInput);
                    } else {
                        orders.forEach(o ->
                                System.out.println("Order ID: " + o.getOrderId() +
                                        ", User ID: " + o.getUserId() +
                                        ", Status: " + o.getStatus() +
                                        ", Total: NT$" + o.getTotalAmount() +
                                        ", Date: " + o.getOrderDate()));
                    }

                    auditLogService.log("Order", "READ", "Viewed orders by status: " + statusInput, user.getUserId());
                    break;
                case "3":
                    try {
                        System.out.println("Enter Start Date (yyyy-MM-dd):");
                        String startStr = scanner.nextLine();
                        System.out.println("Enter End Date (yyyy-MM-dd):");
                        String endStr = scanner.nextLine();
                        Date startDate = Date.valueOf(startStr);
                        Date endDate = Date.valueOf(endStr);

                        orderService.findByDateRange(startDate, endDate).forEach(o ->
                                System.out.println("Order ID: " + o.getOrderId() +
                                        ", User ID: " + o.getUserId() +
                                        ", Status: " + o.getStatus() +
                                        ", Total: NT$" + o.getTotalAmount() +
                                        ", Date: " + o.getOrderDate()));
                        auditLogService.log("Order", "READ", "Viewed orders from " + startDate + " to " + endDate, user.getUserId());
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid Date Format. Use yyyy-MM-dd.");
                    }
                    break;
//                case "4":
//                    System.out.println("Enter Order ID to View Details:");
//                    int oid = scanner.nextInt();
//                    scanner.nextLine();
//                    orderService.findOrderDetailsByOrderId(oid).forEach(d ->
//                            System.out.println("Product ID: " + d.getProductId() +
//                                    ", Quantity: " + d.getQuantity() +
//                                    ", Price: NT$" + d.getPrice()));
//                    break;
                case "4":
                    System.out.println("All Orders:");
                    for (var o : orderService.getAll()) {
                        String userName = "N/A";
                        try {
                            userName = userService.getById(o.getUserId()).getName();
                        } catch (ServiceException e) {
                            System.out.println("Failed to get user name for userId " + o.getUserId());
                        }

                        System.out.println("Order ID: " + o.getOrderId() +
                                ", Order Code: " + o.getOrderCode() +
                                ", User ID: " + o.getUserId() +
                                ", User: " + userName +
                                ", Status: " + o.getStatus() +
                                ", Total: NT$" + o.getTotalAmount() +
                                ", Date: " + o.getOrderDate());
                    }


                    System.out.print("Enter Order ID to view details: ");
                    int targetOrderId = scanner.nextInt();
                    scanner.nextLine();

                    OrderDetailDaoImpl detailDao = new OrderDetailDaoImpl(conn);
                    detailDao.printOrderDetailsByOrderId(targetOrderId);
                    auditLogService.log("Order", "READ", "Viewed order detail for order ID: " + targetOrderId, user.getUserId());
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid Option");
            }
        }
    }
}