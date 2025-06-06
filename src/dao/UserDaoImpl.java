package dao;

import model.User;
import model.UserRole;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements UserDao {
    private final Connection conn;

    public UserDaoImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public boolean insert(User user) throws SQLException {
        String sql = "INSERT INTO User (name, email, password, birthday, register_date, role) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHashForDAO());
            ps.setDate(4, new java.sql.Date(user.getBirthday().getTime()));
            ps.setDate(5, new java.sql.Date(user.getRegisterDate().getTime()));
            ps.setString(6, user.getRole().name());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(User user) throws SQLException {
        String sql = "UPDATE User SET name=?, email=?, birthday=? WHERE user_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setDate(3, new java.sql.Date(user.getBirthday().getTime()));
            ps.setInt(4, user.getUserId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteById(int id) throws SQLException {
        String sql = "DELETE FROM User WHERE user_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public User getById(int id) throws SQLException {
        String sql = "SELECT * FROM User WHERE user_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                    return mapResultSet(rs);
            }
        }
        return null;
    }

    @Override
    public List<User> getAll() throws SQLException {
        String sql = "SELECT * FROM User";
        List<User> users = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(mapResultSet(rs));
            }
        }
        return users;
    }

//    @Override
//    public User findByEmail(String email) throws SQLException {
//        String sql = "SELECT * FROM User WHERE email=?";
//        try (PreparedStatement ps = conn.prepareStatement(sql)) {
//            ps.setString(1, email);
//            try (ResultSet rs = ps.executeQuery()) {
//                if (rs.next()) {
//                    return mapResultSet(rs);
//                }
//            }
//        }
//        return null;
//    }

    @Override
    public List<User> findByBirthdayMonth(int month) throws SQLException {
        String sql = "SELECT * FROM User WHERE MONTH(birthday) = ?";
        List<User> users = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, month);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                users.add(mapResultSet(rs));
            }
        }
        return users;
    }

//    @Override
//    public boolean existsByEmail(String email) throws SQLException {
//        String sql = "SELECT COUNT(*) FROM User WHERE email=?";
//        try (PreparedStatement ps = conn.prepareStatement(sql)) {
//            ps.setString(1, email);
//            try (ResultSet rs = ps.executeQuery()) {
//                if (rs.next()) {
//                    return rs.getInt(1) > 0;
//                }
//            }
//        }
//        return false;
//    }

    @Override
    public User getByEmailAndPassword(String email, String passwordHash) throws SQLException {
        String sql = "SELECT * FROM User WHERE email=? AND password=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, passwordHash);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                    return mapResultSet(rs);
            }
        }
        return null;
    }

    // DAO 內部專用 Mapping，永遠不傳密碼
    private User mapResultSet(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("user_id"),
                rs.getString("name"),
                rs.getString("email"),
                "******", // 不暴露密碼
                rs.getDate("birthday"),
                rs.getDate("register_date"),
                UserRole.valueOf(rs.getString("role"))
        );
    }

    @Override
    public BigDecimal getAccumulatedThreshold(int userId) throws SQLException {
        String sql = "SELECT threshold_accumulated FROM User WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                BigDecimal acc = rs.getBigDecimal("threshold_accumulated");
                return acc != null ? acc : BigDecimal.ZERO;
            } else {
                throw new SQLException("User not found: ID = " + userId);
            }
        }
    }

    @Override
    public boolean updateAccumulatedThreshold(int userId, BigDecimal newAmount) throws SQLException {
        String sql = "UPDATE User SET threshold_accumulated = ? WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBigDecimal(1, newAmount);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        }
    }
}
