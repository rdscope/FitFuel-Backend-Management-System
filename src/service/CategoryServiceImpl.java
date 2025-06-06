package service;

import dao.CategoryDao;
import dao.CategoryDaoImpl;
import model.Category;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class CategoryServiceImpl implements SERVICE<Category> {
    private final CategoryDao categoryDao;

    public CategoryServiceImpl(Connection conn) {
        this.categoryDao = new CategoryDaoImpl(conn);
    }

    @Override
    public boolean insert(Category category) throws ServiceException {
        try {
            if (!categoryDao.insert(category)) {
                throw new ServiceException("Failed Creating Category...");
            }
            return true;
        } catch (SQLException e) {
            throw new ServiceException("SQL Found Error (Create Category): ", e);
        }
    }

    @Override
    public boolean update(Category category) throws ServiceException {
        try {
            if (!categoryDao.update(category)) {
                throw new ServiceException("Failed Updating Category...");
            }
            return true;
        } catch (SQLException e) {
            throw new ServiceException("SQL Found Error (Update Category): ", e);
        }
    }

    @Override
    public boolean deleteById(int id) throws ServiceException {
        try {
            if (!categoryDao.deleteById(id)) {
                throw new ServiceException("Failed Deleting Category...");
            }
            return true;
        } catch (SQLException e) {
            throw new ServiceException("SQL Found Error (Delete Category): ", e);
        }
    }

    @Override
    public Category getById(int id) throws ServiceException {
        try {
            Category category = categoryDao.getById(id);
            if (category == null) {
                throw new ServiceException("Category Not Found");
            }
            return category;
        } catch (SQLException e) {
            throw new ServiceException("Failed Searching Category...", e);
        }
    }

    @Override
    public List<Category> getAll() throws ServiceException {
        try {
            return categoryDao.getAll();
        } catch (SQLException e) {
            throw new ServiceException("Failed Grabbing Categories...", e);
        }
    }
}