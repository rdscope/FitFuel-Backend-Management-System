package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/fitfuel_fitness_food_db?serverTimezone=Asia/Taipei&useSSL=false";
    private static final String USER = "root";
    private static final String PASSWORD = "my-secret-pw";

    public static Connection getConnection() throws SQLException{
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Successfully Connect to DB");
            return connection;
        }catch (ClassNotFoundException e){
            System.out.println("Cannot Find MySQL Driver: " + e.getMessage());
            return null;
        }catch (SQLException e){
            System.out.println("Failed Connecting to DB: " + e.getMessage());
            return null;
        }
    }

    private static void closeConnection(Connection connection){
        if(connection != null){
            try{
                connection.close();
                System.out.println("DB Connection Closed");
            }catch(SQLException e){
                System.out.println("Field Closing the DB Connection ： " + e.getMessage());
            }
        }
    }
}
