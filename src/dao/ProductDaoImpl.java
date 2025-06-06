package dao;

import model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDaoImpl implements ProductDao {
    private Connection conn;

    public ProductDaoImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public boolean insert(Product product) throws SQLException {
        String sql = "INSERT INTO Product (name, category_id, price, stock_quantity, is_made_to_order) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, product.getName());
            ps.setInt(2, product.getCategoryId());
            ps.setBigDecimal(3, product.getPrice());
            ps.setInt(4, product.getStockQuantity());
            ps.setBoolean(5, product.isMadeToOrder());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(Product product) throws SQLException {
        String sql = "UPDATE Product SET name=?, category_id=?, price=?, stock_quantity=?, is_made_to_order=? WHERE product_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, product.getName());
            ps.setInt(2, product.getCategoryId());
            ps.setBigDecimal(3, product.getPrice());
            ps.setInt(4, product.getStockQuantity());
            ps.setBoolean(5, product.isMadeToOrder());
            ps.setInt(6, product.getProductId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteById(int id) throws SQLException {
        String sql = "DELETE FROM Product WHERE product_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Product getById(int id) throws SQLException {
        String sql = "SELECT * FROM Product WHERE product_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Product(
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getInt("category_id"),
                        rs.getBigDecimal("price"),
                        rs.getInt("stock_quantity"),
                        rs.getBoolean("is_made_to_order")
                );
            }
            return null;
        }
    }

    @Override
    public List<Product> getAll() throws SQLException {
        String sql = "SELECT * FROM Product";
        List<Product> products = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                products.add(new Product(
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getInt("category_id"),
                        rs.getBigDecimal("price"),
                        rs.getInt("stock_quantity"),
                        rs.getBoolean("is_made_to_order")
                ));
            }
        }
        return products;
    }

    @Override
    public List<Product> findByCategory(int categoryId) throws SQLException {
        String sql = "SELECT * FROM Product WHERE category_id=?";
        List<Product> products = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                products.add(mapResultSet(rs));
            }
        }
        return products;
    }

    @Override
    public List<Product> findLowStock(int threshold) throws SQLException {
        String sql = "SELECT * FROM Product WHERE stock_quantity < ?";
        List<Product> products = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, threshold);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                products.add(mapResultSet(rs));
            }
        }
        return products;
    }

    @Override
    public List<Product> findMadeToOrderProducts() throws SQLException {
        String sql = "SELECT * FROM Product WHERE is_made_to_order = TRUE";
        List<Product> result = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(mapResultSet(rs));
            }
        }
        return result;
    }

    private Product mapResultSet(ResultSet rs) throws SQLException {
        return new Product(
                rs.getInt("product_id"),
                rs.getString("name"),
                rs.getInt("category_id"),
                rs.getBigDecimal("price"),
                rs.getInt("stock_quantity"),
                rs.getBoolean("is_made_to_order")
        );
    }
}
