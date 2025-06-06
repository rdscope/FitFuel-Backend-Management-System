package dao;

import model.Order;
import model.OrderDetail;
import model.PaymentMethod;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.math.BigDecimal;

public class OrderDaoImpl implements OrderDao {
    private Connection conn;

    public OrderDaoImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public boolean insert(Order order) throws SQLException {
        String sql = "INSERT INTO `Order` (order_code, user_id, order_date, status, total_amount) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, order.getOrderCode());
            ps.setInt(2, order.getUserId());
            // ps.setDate(2, new java.sql.Date(order.getOrderDate().getTime()));
            ps.setTimestamp(3, Timestamp.valueOf(order.getOrderDate()));
            ps.setString(4, order.getStatus());
            ps.setBigDecimal(5, order.getTotalAmount());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(Order order) throws SQLException {
        String sql = "UPDATE `Order` SET status=?, total_amount=? WHERE order_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, order.getStatus());
            ps.setBigDecimal(2, order.getTotalAmount());
            ps.setInt(3, order.getOrderId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteById(int id) throws SQLException {
        String sql = "DELETE FROM `Order` WHERE order_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Order getById(int id) throws SQLException {
        String sql = "SELECT * FROM `Order` WHERE order_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs);
            }
            return null;
        }
    }

    @Override
    public List<Order> getAll() throws SQLException {
        String sql = "SELECT * FROM `Order`";
        List<Order> orders = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                orders.add(mapResultSet(rs));
            }
        }
        return orders;
    }

    @Override
    public List<Order> findByUser(int userId) throws SQLException {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM `Order` WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSet(rs)); // 你原本就有 mapResultSet()
            }
        }
        return list;
    }

    @Override
    public List<Order> findByStatus(String status) throws SQLException {
        String sql = "SELECT * FROM `Order` WHERE status=?";
        List<Order> orders = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                orders.add(mapResultSet(rs));
            }
        }
        return orders;
    }

//    @Override
//    public List<Order> findByDateRange(Date start, Date end) throws SQLException {
//        // 將 java.sql.Date 轉換為 java.time.LocalDate
//        LocalDate startLocal = start.toLocalDate();
//        LocalDate endLocal = end.toLocalDate().plusDays(1); // 包含整天
//
//        String sql = "SELECT * FROM `Order` WHERE order_date >= ? AND order_date < ?";
//        List<Order> orders = new ArrayList<>();
//        try (PreparedStatement ps = conn.prepareStatement(sql)) {
//            ps.setTimestamp(1, Timestamp.valueOf(startLocal.atStartOfDay()));
//            ps.setTimestamp(2, Timestamp.valueOf(endLocal.atStartOfDay())); // end + 1 天
//            ResultSet rs = ps.executeQuery();
//            while (rs.next()) {
//                orders.add(mapResultSet(rs));
//            }
//        }
//        return orders;
//    }
    @Override
    public List<Order> findByDateRange(Date start, Date end) throws SQLException {
        // 把 end 加一天（查詢到 < end+1）
        long oneDayMillis = 24 * 60 * 60 * 1000L;
        Date endPlusOne = new Date(end.getTime() + oneDayMillis);

        String sql = "SELECT * FROM `Order` WHERE order_date >= ? AND order_date < ?";
        List<Order> orders = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, new Timestamp(start.getTime()));
            ps.setTimestamp(2, new Timestamp(endPlusOne.getTime()));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                orders.add(mapResultSet(rs));
            }
        }
        return orders;
    }


    @Override
    public BigDecimal sumTotalAmountByUserWithStatus(int userId, String status) throws SQLException {
        String sql = "SELECT SUM(total_amount) FROM `Order` WHERE user_id = ? AND status = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, status);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal(1) != null ? rs.getBigDecimal(1) : BigDecimal.ZERO;
            }
            return BigDecimal.ZERO;
        }
    }

    private Order mapResultSet(ResultSet rs) throws SQLException {
        return new Order(
                rs.getInt("order_id"),
                rs.getString("order_code"),
                rs.getInt("user_id"),
                // rs.getDate("order_date"),
                rs.getTimestamp("order_date").toLocalDateTime(),
                rs.getString("status"),
                rs.getBigDecimal("total_amount")
        );
    }

    @Override
    public List<Order> findUnpaidByUser(int userId) throws SQLException {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM `Order` WHERE user_id=? AND status='Unpaid'";
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
    public boolean updateOrderStatus(int orderId, String status) throws SQLException {
        String sql = "UPDATE `Order` SET status=? WHERE order_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateOrderTotal(int orderId, BigDecimal newTotal) throws SQLException {
        String sql = "UPDATE `Order` SET total_amount = ? WHERE order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, newTotal);
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public int countOrdersByUserAndDate(int userId, LocalDate date) throws SQLException {
        String sql = "SELECT COUNT(*) FROM `Order` WHERE user_id = ? AND DATE(order_date) = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, java.sql.Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    @Override
    public Order findLastCreatedByUser(int userId) throws SQLException {
        String sql = "SELECT * FROM `Order` WHERE user_id = ? ORDER BY order_id DESC LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs); // 你應該已經有這個方法
            }
        }
        return null;
    }

    @Override
    public Order findByOrderCode(String orderCode) throws SQLException {
        String sql = "SELECT * FROM `Order` WHERE order_code = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, orderCode);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractOrderFromResultSet(rs);
            }
        }
        return null;
    }
    private Order extractOrderFromResultSet(ResultSet rs) throws SQLException {
        int orderId = rs.getInt("order_id");
        String orderCode = rs.getString("order_code");
        int userId = rs.getInt("user_id");
        LocalDateTime orderTime = rs.getTimestamp("order_date").toLocalDateTime();
        String status = rs.getString("status");
        BigDecimal totalAmount = rs.getBigDecimal("total_amount");

        return new Order(orderId, orderCode, userId, orderTime, status, totalAmount);
    }

    @Override
    public boolean updatePaymentMethod(int orderId, PaymentMethod method) throws SQLException {
        String sql = "UPDATE `Order` SET payment_method = ? WHERE order_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, method.name());
            stmt.setInt(2, orderId);
            return stmt.executeUpdate() == 1;
        }
    }
}
