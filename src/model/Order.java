package model;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;

public class Order {
    private int orderId;
    private final String orderCode;
    private int userId;
    private LocalDateTime orderDate;
    private String status;
    private BigDecimal totalAmount;
    private PaymentMethod paymentMethod;

    public Order(String orderCode) {
        this.orderCode = orderCode;
    }

    public Order(int orderId, String orderCode, int userId, LocalDateTime orderDate, String status, BigDecimal totalAmount) {
        this.orderId = orderId;
        this.orderCode = orderCode;
        this.userId = userId;
        this.orderDate = orderDate;
        this.status = status;
        this.totalAmount = totalAmount;

    }

    public int getOrderId() {return orderId;}
    public String getOrderCode() { return orderCode; }
    public int getUserId() {return userId;}
    public LocalDateTime getOrderDate() {return orderDate;}
    public String getStatus() {return status;}
    public void setStatus(String status) {this.status = status;}
    public BigDecimal getTotalAmount() {return totalAmount;}
    public PaymentMethod getPaymentMethod() {return paymentMethod;}
    public void setPaymentMethod(PaymentMethod paymentMethod) {this.paymentMethod = paymentMethod;}


    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", userId=" + userId +
                ", orderDate=" + orderDate +
                ", status='" + status + '\'' +
                ", totalAmount=" + totalAmount +
                '}';
    }
}