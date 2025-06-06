package model;

import java.math.BigDecimal;

public class RawMaterial {
    private int rawMaterialId;
    private String name;
    private String supplier;
    private BigDecimal stockQuantity; // 食材或材料庫存（如雞胸肉, 蛋白粉）
    private BigDecimal lowStockThreshold;

    public RawMaterial() {}

    public RawMaterial(int rawMaterialId, String name, String supplier, BigDecimal stockQuantity,BigDecimal lowStockThreshold) {
        this.rawMaterialId = rawMaterialId;
        this.name = name;
        this.supplier = supplier;
        this.stockQuantity = stockQuantity;
        this.lowStockThreshold = lowStockThreshold;
    }

    public int getRawMaterialId() {return rawMaterialId;}
    public String getName() {return name;}
    public String getSupplier() {return supplier;}
    public BigDecimal getStockQuantity() {return stockQuantity;}

    public void setStockQuantity(BigDecimal stockQuantity) {
        if (stockQuantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative.");
        }
        this.stockQuantity = stockQuantity;
    }

    public BigDecimal getLowStockThreshold() {return lowStockThreshold;}
    public void setName(String name) {this.name = name;}
    public void setSupplier(String supplier) {this.supplier = supplier;}
    public void setLowStockThreshold(BigDecimal lowStockThreshold) {this.lowStockThreshold = lowStockThreshold;}
//    public int getRawMaterialId() {return rawMaterialId;3}
//    public String getName() {return name;}
//    public void setName(String name) {this.name = name;}
//    public String getSupplier() {return supplier;}
//    public void setSupplier(String supplier) {this.supplier = supplier;}
//    public int getStockQuantity() {return stockQuantity;}
//    protected void setStockQuantity(int stockQuantity) {this.stockQuantity = stockQuantity;}

    @Override
    public String toString() {
        return "RawMaterial{" +
                "rawMaterialId=" + rawMaterialId +
                ", name='" + name + '\'' +
                ", supplier='" + supplier + '\'' +
                ", stockQuantity=" + stockQuantity +
                '}';
    }
}