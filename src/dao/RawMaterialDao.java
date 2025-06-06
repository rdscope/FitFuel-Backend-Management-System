package dao;

import model.RawMaterial;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public interface RawMaterialDao extends DAO<RawMaterial> {
    boolean replenishStock(int rawMaterialId, BigDecimal amount) throws SQLException;

    boolean reduceStock(int rawMaterialId, BigDecimal amount) throws SQLException;
}
