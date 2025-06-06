package Main;

import model.*;
import service.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class DiscountManagementMenu {

    public static void show(Connection conn, Scanner scanner, DiscountServiceImpl discountService, AuditLogServiceImpl auditLogService, User user) throws ServiceException {
        UserDiscountServiceImpl userDiscountService = new UserDiscountServiceImpl(conn);
        UserServiceImpl userService = new UserServiceImpl(conn);


        while (true) {
            System.out.println("\n===== DISCOUNT MANAGEMENT MENU =====");
            System.out.println("1. Create New Discount");
            System.out.println("2. List All Discounts");
            System.out.println("3. Delete Discount by ID");
            System.out.println("4. Show All Active Promotions (Today)");
            System.out.println("5. View User Discount History");
            System.out.println("6. Assign Discounts to User");
            System.out.println("7. Search users by birthday month");
            System.out.println("0. Back to Admin Menu");
            System.out.print("CHOOSE... ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    createDiscount(scanner, discountService, auditLogService, user);
                    break;
                case "2":
                    listAllDiscounts(discountService, auditLogService, user);
                    break;
                case "3":
                    listAllDiscounts(discountService, auditLogService, user);
                    deleteDiscountById(scanner, discountService, auditLogService, user);
                    break;
                case "4":
                    showAllActivePromotions(discountService, auditLogService, user);
                    break;


                case "5":
                    System.out.println("Enter User ID to view discount history:");
                    int targetUserId = scanner.nextInt();
                    scanner.nextLine();

                    try {
                        DiscountHistory history = discountService.getUserDiscountHistory(targetUserId);
                        List<UserDiscount> personal = history.getPersonalCoupons();
                        List<UsedDiscountCode> used = history.getUsedCodes();

                        System.out.println("=== USER'S PERSONAL DISCOUNT COUPONS ===");
                        if (personal.isEmpty()) {
                            System.out.println("No personal discount records found.");
                        } else {
                            for (UserDiscount ud : personal) {
                                Discount d = discountService.getById(ud.getDiscountId());
                                System.out.println("RECORD ID: " + ud.getId() +
                                        ", DISCOUNT: " + d.getName() +
                                        ", STATUS: " + ud.getStatus() +
                                        ", VALID: " + ud.getStartDate() + " ~ " + ud.getEndDate());
                            }
                        }

                        System.out.println("\n=== USER'S USED DISCOUNT CODES ===");
                        if (used.isEmpty()) {
                            System.out.println("No used discount code records.");
                        } else {
                            for (UsedDiscountCode code : used) {
                                System.out.println("CODE: " + code.getCode() + ", USED AT: " + code.getUsedAt());
                            }
                        }
                        auditLogService.log("UserDiscount", "READ", "Viewed discount history of user ID " + targetUserId, user.getUserId());

                    } catch (ServiceException e) {
                        System.out.println("Failed to retrieve discount history: " + e.getMessage());
                    }
                    break;


                case "6": // 手動指派折扣 給指定用戶
                    try {
                        System.out.println("Available Discounts:");
                        List<Discount> discounts = discountService.getAll();
                        for (Discount dis : discounts) {
                            String type = dis.isPercentage() ? "Percentage" : "Fixed";
                            System.out.println(dis.getDiscountId() + " - " + dis.getName() +
                                    " [" + type + " " + dis.getDiscountAmount() + "]");
                        }

                        System.out.print("Enter Discount ID to assign: ");
                        int discountId = scanner.nextInt();
                        scanner.nextLine();

                        System.out.print("Enter User ID to assign to: ");
                        int userId = scanner.nextInt();
                        scanner.nextLine();

                        Discount selected = discountService.getById(discountId);
                        if (selected == null) {
                            System.out.println("Discount not found.");
                            break;
                        }

                        UserDiscount coupon = new UserDiscount(
                                userId,
                                selected.getDiscountId(),
                                Date.valueOf(LocalDate.now()),
                                Date.valueOf(LocalDate.now().plusMonths(1)),
                                "Unused"
                        );

                        userDiscountService.insert(coupon);
                        System.out.println("Discount assigned to user.");
                        auditLogService.log("UserDiscount", "CREATE", "Assigned discount ID " + discountId + " to user ID " + userId, user.getUserId());

                    } catch (ServiceException e) {
                        System.out.println("Failed to assign discount: " + e.getMessage());
                    }
                    break;


                case "7":
                    System.out.print("Enter month to search birthdays (1-12): ");
                    int targetMonth = scanner.nextInt();
                    scanner.nextLine();

                    if (targetMonth < 1 || targetMonth > 12) {
                        System.out.println("Invalid month. Please enter a value between 1 and 12.");
                        break;
                    }

                    try {
                        List<User> birthdayUsers = userService.findByBirthdayMonth(targetMonth);

                        if (birthdayUsers.isEmpty()) {
                            System.out.println("No users found with birthdays in month " + targetMonth + ".");
                        } else {
                            System.out.println("Users with birthdays in month " + targetMonth + ":");
                            for (User u : birthdayUsers) {
                                System.out.println("User ID: " + u.getUserId() +
                                        ", Name: " + u.getName() +
                                        ", Birthday: " + u.getBirthday());
                            }
                        }
                        auditLogService.log("User", "READ", "Queried birthday users for month " + targetMonth, user.getUserId());

                    } catch (ServiceException e) {
                        System.out.println("Failed to retrieve birthday users: " + e.getMessage());
                    }
                    break;


                case "0":
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    public static void createDiscount(Scanner scanner, DiscountServiceImpl discountService,AuditLogServiceImpl auditLogService, User user) {
        try {
            System.out.println("==== CREATE NEW DISCOUNT ====");
            System.out.print("Discount Name: ");
            String name = scanner.nextLine();

            System.out.print("Discount Code (leave blank if none): ");
            String code = scanner.nextLine().trim();
            if (code.isEmpty()) code = null;

            BigDecimal amount;
            boolean isPercentage;
            while (true) {
                System.out.print("Is it a percentage discount? (true/false): ");
                isPercentage = Boolean.parseBoolean(scanner.nextLine());

                System.out.print("Discount Amount (e.g., 0.15 for 15% or 80 for NT$80): ");
                amount = new BigDecimal(scanner.nextLine());

                if (!isPercentage && amount.compareTo(BigDecimal.ONE) < 0) {
                    System.out.println("Warning: You've set a fixed discount lower than NT$1. Please re-enter.");
                } else if (isPercentage && amount.compareTo(BigDecimal.ONE) >= 1) {
                    System.out.println("Warning: Percentage must be less than 1 (e.g., 0.15 for 15%). Please re-enter.");
                } else {
                    break;
                }
            }

            System.out.print("Is it a single-use discount? (true/false): ");
            boolean isSingleUse = Boolean.parseBoolean(scanner.nextLine());

            System.out.print("Is this a recurring annual discount? (true/false): ");
            boolean isRecurring = Boolean.parseBoolean(scanner.nextLine());

            Discount discount = new Discount();
            discount.setName(name);
            discount.setCode(code);
            discount.setDiscountAmount(amount);
            discount.setPercentage(isPercentage);
            discount.setSingleUse(isSingleUse);
            discount.setRecurring(isRecurring);

            if (isRecurring) {
                System.out.print("Recurring Start Month (1-12): ");
                discount.setRecurringStartMonth(Integer.parseInt(scanner.nextLine()));
                System.out.print("Recurring Start Day: ");
                discount.setRecurringStartDay(Integer.parseInt(scanner.nextLine()));
                System.out.print("Recurring End Month (1-12): ");
                discount.setRecurringEndMonth(Integer.parseInt(scanner.nextLine()));
                System.out.print("Recurring End Day: ");
                discount.setRecurringEndDay(Integer.parseInt(scanner.nextLine()));
            } else {
                System.out.print("Start Date (yyyy-MM-dd): ");
                discount.setStartDate(Date.valueOf(scanner.nextLine()));
                System.out.print("End Date (yyyy-MM-dd): ");
                discount.setEndDate(Date.valueOf(scanner.nextLine()));
            }

            discountService.insert(discount);
            System.out.println("Discount successfully created!");
            auditLogService.log("Discount", "CREATE", "Created new discount", user.getUserId());

        } catch (Exception e) {
            System.out.println("Failed to create discount: " + e.getMessage());
        }
    }


    public static void listAllDiscounts(DiscountServiceImpl discountService, AuditLogServiceImpl auditLogService, User user) {
        try {
            List<Discount> discounts = discountService.getAll();
            System.out.println("==== ALL DISCOUNTS ====");
            for (Discount d : discounts) {
                System.out.println("[ID: " + d.getDiscountId() + "] " + d.getName() +
                        (d.getCode() != null ? " | Code: " + d.getCode() : "") +
                        " | Amount: " + d.getDiscountAmount() +
                        (d.isPercentage() ? " (percentage)" : " (fixed)") +
                        (d.isRecurring() ? " | Recurring" : " | One-Time") +
                        (d.isSingleUse() ? " | Single Use" : ""));
            }
            auditLogService.log("Discount", "READ", "Viewed all discounts", user.getUserId());

        } catch (Exception e) {
            System.out.println("Failed to list discounts: " + e.getMessage());
        }
    }

    public static void deleteDiscountById(Scanner scanner, DiscountServiceImpl discountService, AuditLogServiceImpl auditLogService, User user) {
        try {
            System.out.print("Enter Discount ID to delete: ");
            int id = Integer.parseInt(scanner.nextLine());
            boolean success = discountService.deleteById(id);
            if (success) {
                System.out.println("Discount deleted successfully.");
                auditLogService.log("Discount", "DELETE", "Deleted discount ID " + id, user.getUserId());
            } else {
                System.out.println("No discount found with ID " + id);
            }
        } catch (Exception e) {
            System.out.println("Failed to delete discount: " + e.getMessage());
        }
    }


    public static void showAllActivePromotions(DiscountServiceImpl discountService, AuditLogServiceImpl auditLogService, User user) {
        try {
            LocalDate today = LocalDate.now();
            List<Discount> active = discountService.getAll().stream()
                    .filter(d -> {
                        if (d.isRecurring()) {
                            LocalDate start = LocalDate.of(today.getYear(), d.getRecurringStartMonth(), d.getRecurringStartDay());
                            LocalDate end = LocalDate.of(today.getYear(), d.getRecurringEndMonth(), d.getRecurringEndDay());
                            if (end.isBefore(start)) {
                                return !today.isBefore(start) || !today.isAfter(end.plusYears(1));
                            } else {
                                return !today.isBefore(start) && !today.isAfter(end);
                            }
                        } else if (d.getStartDate() != null && d.getEndDate() != null) {
                            LocalDate start = d.getStartDate().toLocalDate();
                            LocalDate end = d.getEndDate().toLocalDate();
                            return !today.isBefore(start) && !today.isAfter(end);
                        }
                        return false;
                    })
                    .toList();

            if (active.isEmpty()) {
                System.out.println("No active promotions today.");
            } else {
                System.out.println("Active Promotions Today:");
                for (Discount d : active) {
                    String type = d.isRecurring() ? "[Annual]" : "[One-Time]";
                    System.out.println(type + " " + d.getName() + " (" + d.getDiscountAmount() + ")");
                }
            }
            auditLogService.log("Discount", "READ", "Viewed active promotions today", user.getUserId());

        } catch (Exception e) {
            System.out.println("Failed to find active promotions: " + e.getMessage());
        }
    }

//    public static boolean isTodayWithinRecurringPeriod(Discount d) {
//        LocalDate today = LocalDate.now();
//        LocalDate start = LocalDate.of(today.getYear(), d.getRecurringStartMonth(), d.getRecurringStartDay());
//        LocalDate end = LocalDate.of(today.getYear(), d.getRecurringEndMonth(), d.getRecurringEndDay());
//
//        if (end.isBefore(start)) {
//            // 跨年活動：如 12/15 ~ 01/10
//            return !today.isBefore(start) || !today.isAfter(end.plusYears(1));
//        } else {
//            return !today.isBefore(start) && !today.isAfter(end);
//        }
//    }
}

