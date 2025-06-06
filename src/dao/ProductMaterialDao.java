package dao;

import model.ProductMaterial;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public interface ProductMaterialDao extends DAO<ProductMaterial> {
    List<ProductMaterial> findByProductId(int productId) throws SQLException;
    boolean updateProductMaterialMapping(int productId, int rawMaterialId, BigDecimal newQuantityPerProduct) throws SQLException;

    boolean addProductMaterialMapping(int productId, int rawMaterialId, BigDecimal quantityPerProduct) throws SQLException;
    boolean deleteProductMaterialMapping(int productId, int rawMaterialId) throws SQLException;

}
