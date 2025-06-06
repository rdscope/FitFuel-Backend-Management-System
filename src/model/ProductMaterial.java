package model;

import java.math.BigDecimal;

public class ProductMaterial {
    private int productId;
    private int rawMaterialId;
    private BigDecimal quantityPerProduct; // 每份產品需用原料數量

    public ProductMaterial() {}

    public ProductMaterial(int productId, int rawMaterialId, BigDecimal quantityPerProduct) {
        this.productId = productId;
        this.rawMaterialId = rawMaterialId;
        this.quantityPerProduct = quantityPerProduct;
    }

    public int getProductId() {return productId;}
    public int getRawMaterialId() {return rawMaterialId;}
    public BigDecimal getQuantityPerProduct() {return quantityPerProduct;}
    public void setQuantityPerProduct(BigDecimal quantityPerProduct) {this.quantityPerProduct = quantityPerProduct;}
}