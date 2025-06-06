package model;

import java.math.BigDecimal;

public class Product {
    private int productId;
    private String name;
    private int categoryId;
    private BigDecimal price;
    private int stockQuantity;
    private boolean isMadeToOrder; // 是否為需製作品（飲品/健身餐）

    public Product() {}

    public Product(int productId, String name, int categoryId, BigDecimal price, int stockQuantity, boolean isMadeToOrder) {
        this.productId = productId;
        this.name = name;
        this.categoryId = categoryId;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.isMadeToOrder = isMadeToOrder;
    }

    // Getter
    public int getProductId() {return productId;}
    public String getName() {return name;}
    public int getCategoryId() {return categoryId;}
    public BigDecimal getPrice() {return price;}
    // 可視需求改由 Service 控制減少庫存，不直接開放外部修改
    public int getStockQuantity() {return stockQuantity;}
    public boolean isMadeToOrder() {return isMadeToOrder;}

    // Setter（僅限允許變動的欄位，如庫存）
    public void setStockQuantity(int stockQuantity) {
        if (stockQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative.");
        }
        this.stockQuantity = stockQuantity;
    }

    public void setName(String name) {this.name = name;}
    public void setPrice(BigDecimal price) {this.price = price;}
    public void setCategoryId(int categoryId) {this.categoryId = categoryId;}
    public void setMadeToOrder(boolean madeToOrder) {isMadeToOrder = madeToOrder;}

    //    public int getProductId() {return productId;}
//    public String getName() {return name;}
//    public void setName(String name) {this.name = name;}
//    public int getCategoryId() {return categoryId;}
//    public void setCategoryId(int categoryId) {this.categoryId = categoryId;}
//    public BigDecimal getPrice() {return price;}
//    public void setPrice(BigDecimal price) {this.price = price;}
//    public int getStockQuantity() {return stockQuantity;}
//    // 可視需求改由 Service 控制減少庫存，不直接開放外部修改
//    protected void setStockQuantity(int stockQuantity) {this.stockQuantity = stockQuantity;}
//    public boolean isMadeToOrder() {return isMadeToOrder;}
//    public void setMadeToOrder(boolean madeToOrder) {isMadeToOrder = madeToOrder;}

    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", name='" + name + '\'' +
                ", categoryId=" + categoryId +
                ", price=" + price +
                ", stockQuantity=" + stockQuantity +
                ", isMadeToOrder=" + isMadeToOrder +
                '}';
    }
}