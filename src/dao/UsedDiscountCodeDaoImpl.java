package dao;

import model.UsedDiscountCode;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsedDiscountCodeDaoImpl implements UsedDiscountCodeDao {
    private final Connection conn;

    public UsedDiscountCodeDaoImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public List<UsedDiscountCode> findByUser(int userId) throws SQLException {
        List<UsedDiscountCode> list = new ArrayList<>();
        String sql = "SELECT * FROM UsedDiscountCode WHERE user_id = ? ORDER BY used_at DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                UsedDiscountCode record = new UsedDiscountCode(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("code"),
                        rs.getTimestamp("used_at")
                );
                list.add(record);
            }
        }
        return list;
    }

    @Override
    public boolean hasUserUsedCode(int userId, String code) throws SQLException {
        String sql = "SELECT COUNT(*) FROM UsedDiscountCode WHERE user_id = ? AND LOWER(code) = LOWER(?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, code);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    @Override
    public boolean hasUserUsedDiscountId(int userId, int discountId) throws SQLException {
        try {
            String sql = "SELECT COUNT(*) FROM UsedDiscountCode WHERE user_id = ? AND discount_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, discountId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean insert(int userId, String code, int discountId, int orderId) throws SQLException {
        String sql = "INSERT INTO UsedDiscountCode (user_id, code, discount_id, order_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, code);  // 這裡 code 可以為 NULL，用來處理活動折扣
            stmt.setInt(3, discountId);
            stmt.setInt(4, orderId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            // 這裡處理當折扣碼已經使用過的情況
            System.out.println("Record already exists for this discount usage.");
            return true; // 已存在也算成功，不需重複插入
        } catch (SQLException e) {
            System.out.println("Failed to insert used discount code: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
