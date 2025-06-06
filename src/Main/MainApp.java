package Main;

import dao.DBConnection;
import model.User;
import model.UserRole;
import service.UserServiceImpl;
import service.ServiceException;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Scanner;

public class MainApp {
    public static void main(String[] args) {

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                System.out.println("DB Connection Lost!");
                return;
            }
            UserServiceImpl userService = new UserServiceImpl(conn);
            LoginService loginService = new LoginService(userService, conn);

            SignupService signupService = new SignupService(userService);


            Scanner scanner = new Scanner(System.in);

            while (true){
                System.out.println("====== FitFuel HealthyEats Inventory System ======");
                System.out.println("1_ SIGN IN");
                System.out.println("2_ SIGN UP");
                System.out.println("0_ EXIT");
                System.out.print("CHOOSE...");

                int choice = scanner.nextInt();
                switch(choice){
                    case 1:
                        User user = loginService.login();
                        if (user != null) {
                            System.out.println("Approved User: " + user.getName() + " (" + user.getRole() + ")");
                            if (user.getRole() == UserRole.admin) {
                                AdminMenu.show(conn, user);
                            } else {
                                UserMenu.show(conn, user);
                            }
                        } else {
                            System.out.println("Failed Logging In, Please Try Again...");
                        }
                        break;

                    case 2:
                        signupService.signup();
                        break;

                    case 0:
                        System.out.println("Goodbye!");
                        return;

                    default:
                        System.out.println("Invalid Choice");
                }
            }
        }catch(SQLException e){
            System.out.println("Failed Connecting Database: " + e.getMessage());
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }
}