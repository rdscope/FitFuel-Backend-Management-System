package service;

import dao.UserDao;
import dao.UserDaoImpl;
import model.User;
import service.SERVICE;
import service.ServiceException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UserServiceImpl implements SERVICE<User> {
    private final UserDao userDao;

    public UserServiceImpl(Connection conn) {
        this.userDao = new UserDaoImpl(conn);
    }

    @Override
    public boolean insert(User user) throws ServiceException {
        try {
            if (!userDao.insert(user)) {
                throw new ServiceException("Failed Creating Membership...");
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace(); // 顯示詳細錯誤
            throw new ServiceException("SQL Found Error (Create Membership): " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(User user) throws ServiceException {
        try {
            if (!userDao.update(user)) {
                throw new ServiceException("Failed Updating Membership...");
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServiceException("SQL Found Error (Update Membership): " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteById(int id) throws ServiceException {
        try {
            if (!userDao.deleteById(id)) {
                throw new ServiceException("Failed Deleting Membership...");
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServiceException("SQL Found Error (Delete Membership): " + e.getMessage(), e);
        }
    }

    @Override
    public User getById(int id) throws ServiceException {
        try {
            User user = userDao.getById(id);
            if (user == null) {
                throw new ServiceException("Member Not Found");
            }
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServiceException("Failed Searching Membership..." + e.getMessage(), e);
        }
    }

    @Override
    public List<User> getAll() throws ServiceException {
        try {
            return userDao.getAll();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServiceException("Failed Grabbing Memberships..." + e.getMessage(), e);
        }
    }

    public List<User> findByBirthdayMonth(int month) throws ServiceException {
        try {
            return userDao.findByBirthdayMonth(month);
        } catch (SQLException e) {
            throw new ServiceException("Failed to find users by birthday month", e);
        }
    }


    public User getByEmailAndPassword(String email, String hashedPassword) throws ServiceException {
        try{
            return userDao.getByEmailAndPassword(email, hashedPassword);
        }catch(SQLException e){
            e.printStackTrace();
            throw new ServiceException("Failed Logging In..." + e.getMessage(), e);
        }
    }
}
