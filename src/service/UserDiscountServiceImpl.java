package service;

import dao.UserDiscountDao;
import dao.UserDiscountDaoImpl;
import model.UserDiscount;
import service.ServiceException;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class UserDiscountServiceImpl implements SERVICE<UserDiscount>{
    private final UserDiscountDao userDiscountDao;

    public UserDiscountServiceImpl(Connection conn) {
        this.userDiscountDao = new UserDiscountDaoImpl(conn);
    }

    @Override
    public boolean insert(UserDiscount userDiscount) throws ServiceException {
        try {
            if (!userDiscountDao.insert(userDiscount)) {
                throw new ServiceException("Failed Creating MemberDiscount...");
            }
            return true;
        } catch (SQLException e) {
            throw new ServiceException("SQL Found Error (Create MemberDiscount): ", e);
        }
    }

    @Override
    public boolean update(UserDiscount userDiscount) throws ServiceException {
        throw new ServiceException("Unsupported");
    }

    @Override
    public boolean deleteById(int id) throws ServiceException {
        throw new ServiceException("Unsupported, Try deleteByUserAndDiscount");
    }

    @Override
    public UserDiscount getById(int id) throws ServiceException {
        try {
            UserDiscount result = userDiscountDao.getById(id);
            if (result == null) {
                throw new ServiceException("UserDiscount record not found with ID: " + id);
            }
            return result;
        } catch (SQLException e) {
            throw new ServiceException("SQL Error while retrieving UserDiscount by ID", e);
        }
    }

    @Override
    public List<UserDiscount> getAll() throws ServiceException {
        try {
            return userDiscountDao.getAll();
        } catch (SQLException e) {
            throw new ServiceException("Failed Grabbing MemberDiscount...", e);
        }
    }

    public boolean deleteByUserAndDiscount(int userId, int discountId) throws ServiceException {
        try {
            if (!userDiscountDao.deleteByUserAndDiscount(userId, discountId)) {
                throw new ServiceException("Failed Deleting MemberDiscount...");
            }
            return true;
        } catch (SQLException e) {
            throw new ServiceException("SQL Found Error (Delete MemberDiscount): ", e);
        }
    }

    public UserDiscount getByUserAndDiscount(int userId, int discountId) throws ServiceException {
        try {
            UserDiscount userDiscount = userDiscountDao.getByUserAndDiscount(userId, discountId);
            if (userDiscount == null) {
                throw new ServiceException("MemberDiscount Not Found");
            }
            return userDiscount;
        } catch (SQLException e) {
            throw new ServiceException("Failed Searching MemberDiscount...", e);
        }
    }

    public List<UserDiscount> findByUser(int userId) throws ServiceException {
        try {
            return userDiscountDao.findByUser(userId);
        } catch (SQLException e) {
            throw new ServiceException("Failed Grabbing User's MemberDiscounts...", e);
        }
    }

    public List<UserDiscount> findValidByUser(int userId, LocalDate today) throws ServiceException {
        try {
            return userDiscountDao.findValidByUser(userId, today);
        } catch (SQLException e) {
            throw new ServiceException("Failed to find valid discounts by user", e);
        }
    }

    public boolean updateStatus(int userDiscountId, String status) throws ServiceException {
        try {
            return userDiscountDao.updateStatus(userDiscountId, status);
        } catch (SQLException e) {
            throw new ServiceException("Failed updating user discount status", e);
        }
    }

}