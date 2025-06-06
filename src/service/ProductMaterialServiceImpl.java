package service;

import dao.*;
import model.Product;
import model.ProductMaterial;
import model.RawMaterial;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ProductMaterialServiceImpl implements SERVICE<ProductMaterial> {
    private ProductMaterialDao productMaterialDao;
    private RawMaterialDao rawMaterialDao;


    public ProductMaterialServiceImpl(Connection conn) {
        this.productMaterialDao = new ProductMaterialDaoImpl(conn);
        this.rawMaterialDao = new RawMaterialDaoImpl(conn);
    }


    @Override
    public boolean insert(ProductMaterial pm) throws ServiceException {
        try {
            return productMaterialDao.insert(pm);
        } catch (SQLException e) {
            throw new ServiceException("Failed to insert product material", e);
        }
    }

    @Override
    public boolean update(ProductMaterial t) throws ServiceException {
        throw new ServiceException("Unsupported");
    }

    @Override
    public boolean deleteById(int id) throws ServiceException {
        throw new ServiceException("Unsupported");
    }

    @Override
    public ProductMaterial getById(int id) throws ServiceException {
        throw new ServiceException("Unsupported, Try findByProductId");
    }

    @Override
    public List<ProductMaterial> getAll() throws ServiceException {
        throw new ServiceException("Unsupported");
    }

    // 查產品所需原料
    public List<ProductMaterial> findByProductId(int productId) throws ServiceException {
        try {
            return productMaterialDao.findByProductId(productId);
        } catch (SQLException e) {
            throw new ServiceException("Failed Searching ProductMaterial...", e);
        }
    }

//    // 扣除原料庫存（這裡一定要用 getQuantityPerProduct()）
//    public void consumeMaterials(int productId) throws ServiceException {
//        try {
//            List<ProductMaterial> materials = productMaterialDao.findByProductId(productId);
//            for (ProductMaterial pm : materials) {
//                boolean updated = productMaterialDao.updateMaterialUsage(pm.getRawMaterialId(), pm.getQuantityPerProduct());
//                if (!updated) {
//                    throw new ServiceException("RAW ID: " + pm.getRawMaterialId() + " is OUT OF STOCK");
//                }
//            }
//        } catch (SQLException e) {
//            throw new ServiceException("Deduction Failed?", e);
//        }
//    }

    public void updateProductMaterialMapping(int productId, int rawMaterialId, BigDecimal quantityPerProduct) throws ServiceException {
        try {
            if (!productMaterialDao.updateProductMaterialMapping(productId, rawMaterialId, quantityPerProduct)) {
                throw new ServiceException("Failed to update product material mapping");
            }
        } catch (SQLException e) {
            throw new ServiceException("SQL Error updating product material mapping", e);
        }
    }

    public void addProductMaterialMapping(int productId, int rawMaterialId, BigDecimal quantityPerProduct) throws ServiceException {
        try {
            // 檢查是否為 MadeToOrder 商品
            ProductDao productDao = new ProductDaoImpl(DBConnection.getConnection());
            Product product = productDao.getById(productId);
            if (!product.isMadeToOrder()) {
                throw new ServiceException("This product is not made-to-order and should not bind raw materials.");
            }

            if (!productMaterialDao.addProductMaterialMapping(productId, rawMaterialId, quantityPerProduct)) {
                throw new ServiceException("Failed to Add Product Material Mapping");
            }
        } catch (SQLException e) {
            throw new ServiceException("SQL Error Adding Mapping", e);
        }
    }


    public void deleteProductMaterialMapping(int productId, int rawMaterialId) throws ServiceException {
        try {
            if (!productMaterialDao.deleteProductMaterialMapping(productId, rawMaterialId)) {
                throw new ServiceException("Failed to Delete Product Material Mapping");
            }
        } catch (SQLException e) {
            throw new ServiceException("SQL Error Deleting Mapping", e);
        }
    }

    // ProductMaterialServiceImpl.java
    public int calculateCanMake(int productId) throws ServiceException {
        try {
            List<ProductMaterial> materials = productMaterialDao.findByProductId(productId);
            if (materials.isEmpty()) return 0;

            int canMake = Integer.MAX_VALUE;

            for (ProductMaterial pm : materials) {
                RawMaterial rm = rawMaterialDao.getById(pm.getRawMaterialId());
                if (rm == null) return 0;

                BigDecimal possible = rm.getStockQuantity()
                        .divide(pm.getQuantityPerProduct(), 0, BigDecimal.ROUND_DOWN);
                if (possible.intValue() <= 0) return 0;

                canMake = Math.min(canMake, possible.intValue());
            }

            return canMake;
        } catch (SQLException e) {
            throw new ServiceException("Failed to calculate canMake quantity", e);
        }
    }
}
