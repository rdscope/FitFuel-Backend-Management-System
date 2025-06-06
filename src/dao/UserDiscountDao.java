package dao;

import model.UserDiscount;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface UserDiscountDao extends DAO<UserDiscount> {
    boolean deleteByUserAndDiscount(int userId, int discountId) throws SQLException;
    UserDiscount getByUserAndDiscount(int userId, int discountId) throws SQLException;
    List<UserDiscount> findByUser(int userId) throws SQLException;
    List<UserDiscount> findValidByUser(int userId, LocalDate today) throws SQLException;
    public boolean updateStatus(int userDiscountId, String status) throws SQLException;
}
