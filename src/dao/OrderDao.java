package dao;

import model.Order;
import model.OrderDetail;
import model.PaymentMethod;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.math.BigDecimal;

public interface OrderDao extends DAO<Order> {
    List<Order> findByUser(int userId) throws SQLException;

    List<Order> findByStatus(String status) throws SQLException;

    List<Order> findByDateRange(Date start, Date end) throws SQLException;

    BigDecimal sumTotalAmountByUserWithStatus(int userId, String status) throws SQLException;

    List<Order> findUnpaidByUser(int userId) throws SQLException;

    boolean updateOrderStatus(int orderId, String status) throws SQLException;

    int countOrdersByUserAndDate(int userId, LocalDate date) throws SQLException;
    boolean updateOrderTotal(int orderId, BigDecimal newTotal) throws SQLException;

    Order findLastCreatedByUser(int userId) throws SQLException;

    Order findByOrderCode(String orderCode) throws SQLException;

    boolean updatePaymentMethod(int orderId, PaymentMethod method) throws SQLException;
}