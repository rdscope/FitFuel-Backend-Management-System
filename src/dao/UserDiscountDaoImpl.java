package dao;

import model.UserDiscount;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserDiscountDaoImpl implements UserDiscountDao {
    private final Connection conn;

    public UserDiscountDaoImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public boolean insert(UserDiscount ud) throws SQLException {
        String sql = "INSERT INTO UserDiscount (user_id, discount_id, start_date, end_date, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ud.getUserId());
            ps.setInt(2, ud.getDiscountId());
            ps.setDate(3, ud.getStartDate());
            ps.setDate(4, ud.getEndDate());
            ps.setString(5, ud.getStatus());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(UserDiscount ud) throws SQLException {
        String sql = "UPDATE UserDiscount SET start_date=?, end_date=?, status=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, ud.getStartDate());
            ps.setDate(2, ud.getEndDate());
            ps.setString(3, ud.getStatus());
            ps.setInt(4, ud.getId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteById(int id) throws SQLException {
        String sql = "DELETE FROM UserDiscount WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public UserDiscount getById(int id) throws SQLException {
        String sql = "SELECT * FROM UserDiscount WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs);
            }
        }
        return null;
    }

    @Override
    public List<UserDiscount> getAll() throws SQLException {
        List<UserDiscount> list = new ArrayList<>();
        String sql = "SELECT * FROM UserDiscount";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        }
        return list;
    }

    @Override
    public boolean deleteByUserAndDiscount(int userId, int discountId) throws SQLException {
        String sql = "DELETE FROM UserDiscount WHERE user_id=? AND discount_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, discountId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public UserDiscount getByUserAndDiscount(int userId, int discountId) throws SQLException {
        String sql = "SELECT * FROM UserDiscount WHERE user_id=? AND discount_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, discountId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs);
            }
        }
        return null;
    }

    @Override
    public List<UserDiscount> findByUser(int userId) throws SQLException {
        String sql = "SELECT * FROM UserDiscount WHERE user_id=?";
        List<UserDiscount> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        }
        return list;
    }

    @Override
    public List<UserDiscount> findValidByUser(int userId, LocalDate today) throws SQLException {
        String sql = "SELECT * FROM UserDiscount WHERE user_id = ? AND start_date <= ? AND end_date >= ? AND status = 'Unused'";
        List<UserDiscount> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(today));
            ps.setDate(3, Date.valueOf(today));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        }

//        System.out.println("[DEBUG] Java CURRENT_DATE = " + java.time.LocalDate.now());
//        System.out.println("[DEBUG] Valid coupons on date = " + today + " → " + list.size());
//        System.out.println("[DEBUG] VALID UserDiscount count for user_id = " + userId + " -> " + list.size());
//        for (UserDiscount ud : list) {
//            System.out.println(" -> Discount ID: " + ud.getDiscountId());
//        }

        return list;
    }

    @Override
    public boolean updateStatus(int userDiscountId, String status) throws SQLException {
        String sql = "UPDATE UserDiscount SET status=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, userDiscountId);
            return ps.executeUpdate() > 0;
        }
    }

    private UserDiscount mapResultSet(ResultSet rs) throws SQLException {
        return new UserDiscount(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getInt("discount_id"),
                rs.getDate("start_date"),
                rs.getDate("end_date"),
                rs.getString("status"),
                rs.getTimestamp("issued_at")
        );
    }
}
