package Main;

import Util.DateUtil;
import Util.PasswordUtil;
import model.Discount;
import model.User;
import model.UserDiscount;
import service.DiscountServiceImpl;
import service.ServiceException;
import service.UserDiscountServiceImpl;
import service.UserServiceImpl;

import java.sql.Connection;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Scanner;

public class LoginService {
    private UserServiceImpl userService;
    private Connection conn;

    public LoginService(UserServiceImpl userService, Connection conn){
        this.userService = userService;
        this.conn = conn;
    }

    public User login(){
        Scanner scanner = new Scanner(System.in);

        while (true){
            System.out.println("Please Enter Your Email: ");
            String email = scanner.nextLine();
            System.out.println("Please Enter Your Password: ");
            String passwordPlain = scanner.nextLine();
            String hashedPassword = PasswordUtil.hashPassword(passwordPlain);

            try{
                User user = userService.getByEmailAndPassword(email, hashedPassword);
                if (user == null) {
                    System.out.println("User Not Found or Password Incorrect, Please Try Again...");
                    // 改為繼續 while，不強制 exit

                    // System.out.println("User Not Found， Please try again,,,");
                    // System.exit(0); // Java 結束整個應用程式的指令

                    // return;
                }else{
                    System.out.println("Welcome Back, " + user.getName());
                    return user;

                }
            }catch(ServiceException e){
                System.out.println("Trouble for Logging In: " + e.getMessage());
                return null; // 異常時退出
            }
        }
    }
}
