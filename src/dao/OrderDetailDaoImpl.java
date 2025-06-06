package dao;

import model.OrderDetail;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailDaoImpl implements OrderDetailDao {
    private final Connection conn;

    public OrderDetailDaoImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public boolean insert(OrderDetail detail) throws SQLException {
        String sql = "INSERT INTO OrderDetail (order_id, product_id, quantity, price, remark) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, detail.getOrderId());
            ps.setInt(2, detail.getProductId());
            ps.setInt(3, detail.getQuantity());
            ps.setBigDecimal(4, detail.getPrice());
            ps.setString(5, detail.getRemark());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(OrderDetail detail) throws SQLException {
        String sql = "UPDATE OrderDetail SET quantity=?, price=?, remark=? WHERE order_detail_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, detail.getQuantity());
            ps.setBigDecimal(2, detail.getPrice());
            ps.setString(3, detail.getRemark());
            ps.setInt(4, detail.getOrderDetailId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteById(int id) throws SQLException {
        String sql = "DELETE FROM OrderDetail WHERE order_detail_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public OrderDetail getById(int id) throws SQLException {
        String sql = "SELECT * FROM OrderDetail WHERE order_detail_id=?";
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
    public List<OrderDetail> getAll() throws SQLException {
        String sql = "SELECT * FROM OrderDetail";
        List<OrderDetail> details = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                details.add(mapResultSet(rs));
            }
        }
        return details;
    }

    @Override
    public List<OrderDetail> findByOrder(int orderId) throws SQLException {
        String sql = "SELECT * FROM OrderDetail WHERE order_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            List<OrderDetail> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
            return list;
        }
    }

    private OrderDetail mapResultSet(ResultSet rs) throws SQLException {
        OrderDetail detail = new OrderDetail(
                rs.getInt("order_detail_id"),
                rs.getInt("order_id"),
                rs.getInt("product_id"),
                rs.getInt("quantity"),
                rs.getBigDecimal("price")
        );
        detail.setRemark(rs.getString("remark"));
        return detail;
    }

//    // 可重複利用的建構邏輯：支援 admin 下單等場景
//    public boolean adminPlaceOrderDetail(int orderId, int productId, int quantity, BigDecimal unitPrice, String remark) {
//        try {
//            OrderDetail detail = buildDetail(orderId, productId, quantity, unitPrice, remark);
//            return insert(detail);
//        } catch (SQLException e) {
//            System.out.println("Admin place order failed: " + e.getMessage());
//            return false;
//        }
//    }
//    public OrderDetail buildDetail(int orderId, int productId, int quantity, BigDecimal unitPrice, String remark) {
//        OrderDetail detail = new OrderDetail();
//        detail.setOrderId(orderId);
//        detail.setProductId(productId);
//        detail.setQuantity(quantity);
//        detail.setPrice(unitPrice);
//        detail.setRemark(remark);
//        return detail;
//    }

    // 整合查詢顯示功能，供 UserMenu / AdminMenu 使用
    public void printOrderDetailsByOrderId(int orderId) {
        String sql = "SELECT d.*, p.name AS product_name FROM OrderDetail d " +
                "JOIN Product p ON d.product_id = p.product_id " +
                "WHERE d.order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

            boolean hasData = false;
            System.out.println("\nOrder Details for Order ID: " + orderId);
            System.out.println("------------------------------------------");

            while (rs.next()) {
                hasData = true;
                String productName = rs.getString("product_name");
                int productId = rs.getInt("product_id");
                int qty = rs.getInt("quantity");
                BigDecimal price = rs.getBigDecimal("price");
                String remark = rs.getString("remark");
                if (remark == null) remark = "-";

                System.out.printf("Product: %-20s | ID: %-5d | Qty: %-3d | Price: NT$%-8.2f | Remark: %s\n",
                        productName, productId, qty, price, remark);
            }

            if (!hasData) {
                System.out.println("No details found for order ID: " + orderId);
            }

        } catch (SQLException e) {
            System.out.println("Failed to load order details: " + e.getMessage());
        }
    }

    @Override
    public boolean updateRemark(int orderDetailId, String remark) throws SQLException {
        String sql = "UPDATE OrderDetail SET remark = ? WHERE order_detail_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, remark);
            ps.setInt(2, orderDetailId);
            return ps.executeUpdate() > 0;
        }
    }
}
