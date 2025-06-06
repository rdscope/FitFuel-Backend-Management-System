package Main;

import Util.PasswordUtil;
import model.User;
import model.UserRole;
import service.ServiceException;
import service.UserServiceImpl;

import java.sql.Date;
import java.util.Scanner;

public class SignupService {
    private final UserServiceImpl userService;

    public SignupService(UserServiceImpl userService){
        this.userService = userService;
    }

    public void signup(){
        Scanner scanner = new Scanner(System.in);

        try{
            System.out.println("Enter Your Name: ");
            String name = scanner.next();
            System.out.println("Enter Your Email: ");
            String email = scanner.next();
            System.out.println("Enter Your Password: ");
            String passwordPlain = scanner.next();
            String hashedPassword = PasswordUtil.hashPassword(passwordPlain);
            // System.out.println("Hashed Password: " + hashedPassword);
            System.out.println("Enter Your Birthday (yyyy-MM-dd): ");
            String birthdayStr = scanner.next();
            Date birthday = Date.valueOf(birthdayStr);

            User user = new User(0, name, email, hashedPassword, birthday, new Date(System.currentTimeMillis()), UserRole.user);
            // 在 Java 端可以給 0 或任何預設值，表示「由資料庫分配」

            // 由 Service 負責呼叫 DAO
            if (userService.insert(user)) {
                System.out.println("Account Created, Please Reenter.");
            } else {
                System.out.println("Account Creation Failed.");
            }
        }catch(ServiceException e){
            System.out.println("Failed SigningUp: " + e.getMessage());
        }catch(IllegalArgumentException e) {
            System.out.println("Invalid Date Format. Please use yyyy-MM-dd.");
        }
    }
}
