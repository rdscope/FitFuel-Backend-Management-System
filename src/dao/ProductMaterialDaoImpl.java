package dao;

import model.ProductMaterial;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductMaterialDaoImpl implements ProductMaterialDao {
    private Connection conn;

    public ProductMaterialDaoImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public boolean insert(ProductMaterial pm) throws SQLException {
        String sql = "INSERT INTO ProductMaterial (product_id, raw_material_id, quantity_per_product) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pm.getProductId());
            ps.setInt(2, pm.getRawMaterialId());
            ps.setBigDecimal(3, pm.getQuantityPerProduct());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(ProductMaterial pm) throws SQLException {
        String sql = "UPDATE ProductMaterial SET quantity_per_product=? WHERE product_id=? AND raw_material_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, pm.getQuantityPerProduct());
            ps.setInt(2, pm.getProductId());
            ps.setInt(3, pm.getRawMaterialId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteById(int id) throws SQLException {
        String sql = "DELETE FROM ProductMaterial WHERE product_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public ProductMaterial getById(int id) throws SQLException {
        String sql = "SELECT * FROM ProductMaterial WHERE product_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new ProductMaterial(
                        rs.getInt("product_id"),
                        rs.getInt("raw_material_id"),
                        rs.getBigDecimal("quantity_per_product")
                );
            }
        }
        return null;
    }

    @Override
    public List<ProductMaterial> getAll() throws SQLException {
        String sql = "SELECT * FROM ProductMaterial";
        List<ProductMaterial> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new ProductMaterial(
                        rs.getInt("product_id"),
                        rs.getInt("raw_material_id"),
                        rs.getBigDecimal("quantity_per_product")
                ));
            }
        }
        return list;
    }

    @Override
    public List<ProductMaterial> findByProductId(int productId) throws SQLException {
        List<ProductMaterial> list = new ArrayList<>();
        String sql = "SELECT * FROM ProductMaterial WHERE product_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new ProductMaterial(
                        rs.getInt("product_id"),
                        rs.getInt("raw_material_id"),
                        rs.getBigDecimal("quantity_per_product")
                ));
            }
        }
        return list;
    }

    @Override
    public boolean updateProductMaterialMapping(int productId, int rawMaterialId, BigDecimal newQuantityPerProduct) throws SQLException {
        String sql = "UPDATE ProductMaterial SET quantity_per_product = ? WHERE product_id = ? AND raw_material_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, newQuantityPerProduct);
            ps.setInt(2, productId);
            ps.setInt(3, rawMaterialId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean addProductMaterialMapping(int productId, int rawMaterialId, BigDecimal quantityPerProduct) throws SQLException {
        String sql = "INSERT INTO ProductMaterial (product_id, raw_material_id, quantity_per_product) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.setInt(2, rawMaterialId);
            ps.setBigDecimal(3, quantityPerProduct);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteProductMaterialMapping(int productId, int rawMaterialId) throws SQLException {
        String sql = "DELETE FROM ProductMaterial WHERE product_id=? AND raw_material_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.setInt(2, rawMaterialId);
            return ps.executeUpdate() > 0;
        }
    }
}
