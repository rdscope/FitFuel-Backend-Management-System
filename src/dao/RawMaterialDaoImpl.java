package dao;

import model.RawMaterial;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RawMaterialDaoImpl implements RawMaterialDao {
    private final Connection conn;

    public RawMaterialDaoImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public boolean insert(RawMaterial material) throws SQLException {
        String sql = "INSERT INTO RawMaterial (name, supplier, stock_quantity, low_stock_threshold) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, material.getName());
            ps.setString(2, material.getSupplier());
            ps.setBigDecimal(3, material.getStockQuantity());
            ps.setBigDecimal(4, material.getLowStockThreshold());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(RawMaterial material) throws SQLException {
        String sql = "UPDATE RawMaterial SET name=?, supplier=?, stock_quantity=?, low_stock_threshold=? WHERE raw_material_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, material.getName());
            ps.setString(2, material.getSupplier());
            ps.setBigDecimal(3, material.getStockQuantity());
            ps.setBigDecimal(4, material.getLowStockThreshold());
            ps.setInt(5, material.getRawMaterialId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteById(int id) throws SQLException {
        String sql = "DELETE FROM RawMaterial WHERE raw_material_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public RawMaterial getById(int id) throws SQLException {
        String sql = "SELECT * FROM RawMaterial WHERE raw_material_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs);
            }
        }
        return null;
    }

    @Override
    public List<RawMaterial> getAll() throws SQLException {
        String sql = "SELECT * FROM RawMaterial";
        List<RawMaterial> materials = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                materials.add(mapResultSet(rs));
            }
        }
        return materials;
    }

    private RawMaterial mapResultSet(ResultSet rs) throws SQLException {
        return new RawMaterial(
                rs.getInt("raw_material_id"),
                rs.getString("name"),
                rs.getString("supplier"),
                rs.getBigDecimal("stock_quantity"),
                rs.getBigDecimal("low_stock_threshold")
        );
    }

    @Override
    public boolean replenishStock(int rawMaterialId, BigDecimal amount) throws SQLException {
        String sql = "UPDATE RawMaterial SET stock_quantity = stock_quantity + ? WHERE raw_material_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, amount);
            ps.setInt(2, rawMaterialId);
            return ps.executeUpdate() > 0;
        }
    }


    @Override
    public boolean reduceStock(int rawMaterialId, BigDecimal amount) throws SQLException {
        String sql = "UPDATE RawMaterial SET stock_quantity = stock_quantity - ? WHERE raw_material_id = ? AND stock_quantity >= ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, amount);
            ps.setInt(2, rawMaterialId);
            ps.setBigDecimal(3, amount);
            return ps.executeUpdate() > 0;
        }
    }
}
