package service;

import dao.RawMaterialDaoImpl;
import model.RawMaterial;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class RawMaterialServiceImpl implements SERVICE<RawMaterial> {
    private final RawMaterialDaoImpl rawMaterialDao;

    public RawMaterialServiceImpl(Connection conn) {
        this.rawMaterialDao = new RawMaterialDaoImpl(conn);
    }

    @Override
    public boolean insert(RawMaterial material) throws ServiceException {
        try {
            if (!rawMaterialDao.insert(material)) {
                throw new ServiceException("Failed Creating Raw...");
            }
            return true;
        } catch (SQLException e) {
            throw new ServiceException("SQL Found Error (Create Raw): ", e);
        }
    }

    @Override
    public boolean update(RawMaterial material) throws ServiceException {
        try {
            if (!rawMaterialDao.update(material)) {
                throw new ServiceException("Failed Updating Raw...");
            }
            return true;
        } catch (SQLException e) {
            throw new ServiceException("SQL Found Error (Update Raw): ", e);
        }
    }

    @Override
    public boolean deleteById(int id) throws ServiceException {
        try {
            if (!rawMaterialDao.deleteById(id)) {
                throw new ServiceException("Failed Deleting Raw...");
            }
            return true;
        } catch (SQLException e) {
            throw new ServiceException("SQL Found Error (Delete Raw): ", e);
        }
    }

    @Override
    public RawMaterial getById(int id) throws ServiceException {
        try {
            RawMaterial material = rawMaterialDao.getById(id);
            if (material == null) {
                throw new ServiceException("Raw Not Found");
            }
            return material;
        } catch (SQLException e) {
            throw new ServiceException("Failed Searching Raw...", e);
        }
    }

    @Override
    public List<RawMaterial> getAll() throws ServiceException {
        try {
            return rawMaterialDao.getAll();
        } catch (SQLException e) {
            throw new ServiceException("Failed Grabbing Raws...", e);
        }
    }

    public void replenishStock(int rawMaterialId, BigDecimal amount) throws ServiceException {
        try {
            if (!rawMaterialDao.replenishStock(rawMaterialId, amount)) {
                throw new ServiceException("Replenish failed. Raw Material not found / insufficient.");
            }
        } catch (SQLException e) {
            throw new ServiceException("SQL Error during replenish stock", e);
        }
    }
}