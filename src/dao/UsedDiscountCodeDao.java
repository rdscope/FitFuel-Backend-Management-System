package dao;

import model.UsedDiscountCode;

import java.sql.SQLException;
import java.util.List;

public interface UsedDiscountCodeDao {
    List<UsedDiscountCode> findByUser(int userId) throws SQLException;
    boolean hasUserUsedCode(int userId, String code) throws SQLException;
    boolean hasUserUsedDiscountId(int userId, int discountId) throws SQLException;
    boolean insert(int userId, String code, int discountId, int orderId) throws SQLException;
}