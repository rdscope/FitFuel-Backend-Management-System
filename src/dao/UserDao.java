package dao;

import model.User;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public interface UserDao extends DAO<User> {
//    User findByEmail(String email) throws SQLException;

    List<User> findByBirthdayMonth(int month) throws SQLException;

//    boolean existsByEmail(String email) throws SQLException;

    User getByEmailAndPassword(String email, String password) throws SQLException;

    BigDecimal getAccumulatedThreshold(int userId) throws SQLException;
    boolean updateAccumulatedThreshold(int userId, BigDecimal newAmount) throws SQLException;
}