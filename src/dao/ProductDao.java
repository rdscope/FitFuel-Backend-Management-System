package dao;

import model.Product;

import java.sql.SQLException;
import java.util.List;

public interface ProductDao extends DAO<Product> {
    List<Product> findByCategory(int categoryId) throws SQLException; // --unfullfilled

    List<Product> findLowStock(int threshold) throws SQLException; // --unfullfilled

    List<Product> findMadeToOrderProducts() throws SQLException;
}