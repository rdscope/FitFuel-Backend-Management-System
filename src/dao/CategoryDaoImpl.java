package dao;

import model.Category;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDaoImpl implements CategoryDao {
    private Connection conn;

    public CategoryDaoImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public boolean insert(Category category) throws SQLException {
        String sql = "INSERT INTO Category (category_name) VALUES (?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category.getCategoryName());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(Category category) throws SQLException {
        String sql = "UPDATE Category SET category_name=? WHERE category_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category.getCategoryName());
            ps.setInt(2, category.getCategoryId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteById(int id) throws SQLException {
        String sql = "DELETE FROM Category WHERE category_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Category getById(int id) throws SQLException {
        String sql = "SELECT * FROM Category WHERE category_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Category(rs.getInt("category_id"), rs.getString("category_name"));
            }
        }
        return null;
    }

    @Override
    public List<Category> getAll() throws SQLException {
        String sql = "SELECT * FROM Category";
        List<Category> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Category(rs.getInt("category_id"), rs.getString("category_name")));
            }
        }
        return list;
    }

}
