package service;

import dao.*;
import model.Product;
import model.ProductMaterial;
import model.RawMaterial;
import service.SERVICE;
import service.ServiceException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ProductServiceImpl implements SERVICE<Product> {
    private final ProductDao productDao;
    private final Connection conn;

    public ProductServiceImpl(Connection conn) {
        this.conn = conn;
        this.productDao = new ProductDaoImpl(conn);
    }

    public Connection getConnection() {
        return conn;
    }

    @Override
    public boolean insert(Product product) throws ServiceException {
        try {
            if (!productDao.insert(product)) {
                throw new ServiceException("Failed Creating Product...");
            }
            return true;
        } catch (SQLException e) {
            throw new ServiceException("SQL Found Error (Create Product): ", e);
        }
    }

    @Override
    public boolean update(Product product) throws ServiceException {
        try {
            if (!productDao.update(product)) {
                throw new ServiceException("Failed Updating Product...");
            }
            return true;
        } catch (SQLException e) {
            throw new ServiceException("SQL Found Error (Update Product): ", e);
        }
    }

    @Override
    public boolean deleteById(int id) throws ServiceException {
        try {
            if (!productDao.deleteById(id)) {
                throw new ServiceException("Failed Deleting Product...");
            }
            return true;
        } catch (SQLException e) {
            throw new ServiceException("SQL Found Error (Delete Product): ", e);
        }
    }

    @Override
    public Product getById(int id) throws ServiceException {
        try {
            Product product = productDao.getById(id);
            if (product == null) {
                throw new ServiceException("Product Not Found");
            }
            return product;
        } catch (SQLException e) {
            throw new ServiceException("Failed Searching Product...", e);
        }
    }

    @Override
    public List<Product> getAll() throws ServiceException {
        try {
            return productDao.getAll();
        } catch (SQLException e) {
            throw new ServiceException("Failed Grabbing Products...", e);
        }
    }

    public List<Product> findMadeToOrderProducts() throws ServiceException {
        try {
            return productDao.findMadeToOrderProducts();
        } catch (SQLException e) {
            throw new ServiceException("Failed to get made-to-order products", e);
        }
    }

    public boolean createProduct(String name, int categoryId, BigDecimal price, boolean isMadeToOrder, int stockQty) throws ServiceException {
        Product product = new Product();
        product.setName(name);
        product.setCategoryId(categoryId);
        product.setPrice(price);
        product.setMadeToOrder(isMadeToOrder);

        // 根據是否現做來決定庫存輸入邏輯
        if (isMadeToOrder) {
            product.setStockQuantity(0); // 表示不使用庫存機制
        } else {
            if (stockQty <= 0) {
                throw new ServiceException("Stock quantity must be greater than 0 for ready-made product.");
            }
            product.setStockQuantity(stockQty); // stockQty 是 int
        }

        return insert(product);
    }

    public int calculateMaxProduction(Connection conn, int productId) throws ServiceException {
        try {
            ProductMaterialDao pmDao = new ProductMaterialDaoImpl(conn);
            RawMaterialDao rawDao = new RawMaterialDaoImpl(conn);

            List<ProductMaterial> materials = pmDao.findByProductId(productId);
            int maxUnits = Integer.MAX_VALUE;

            for (ProductMaterial pm : materials) {
                RawMaterial rm = rawDao.getById(pm.getRawMaterialId());
                if (rm == null) continue;

                BigDecimal stock = rm.getStockQuantity();
                BigDecimal needed = pm.getQuantityPerProduct();

                if (needed.compareTo(BigDecimal.ZERO) <= 0) continue;

                int canMake = stock.divide(needed, RoundingMode.FLOOR).intValue();
                maxUnits = Math.min(maxUnits, canMake);
            }

            return maxUnits == Integer.MAX_VALUE ? 0 : maxUnits;

        } catch (Exception e) {
            throw new ServiceException("Failed to calculate production capacity", e);
        }
    }
}



