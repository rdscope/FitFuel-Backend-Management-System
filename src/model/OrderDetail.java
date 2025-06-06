package model;

import java.math.BigDecimal;

public class OrderDetail {
    private int orderDetailId;
    private int orderId;
    private int productId;
    private int quantity;
    private BigDecimal price;
    private String remark; // 折扣資訊備註欄位

    public OrderDetail() {}

    public OrderDetail(int orderDetailId, int orderId, int productId, int quantity, BigDecimal price) {
        this.orderDetailId = orderDetailId;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    public int getOrderDetailId() { return orderDetailId; }
    public int getOrderId() { return orderId; }
    public int getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public void setOrderId(int orderId) {this.orderId = orderId;}
    public void setProductId(int productId) {this.productId = productId;}

    @Override
    public String toString() {
        return "OrderDetail{" +
                "orderDetailId=" + orderDetailId +
                ", orderId=" + orderId +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", price=" + price +
                ", remark='" + remark + '\'' +
                '}';
    }
}