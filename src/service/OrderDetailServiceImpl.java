package service;

import dao.OrderDetailDao;
import dao.OrderDetailDaoImpl;
import model.OrderDetail;
import service.SERVICE;
import service.ServiceException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class OrderDetailServiceImpl implements SERVICE<OrderDetail> {
    private final OrderDetailDao orderDetailDao;

    public OrderDetailServiceImpl(Connection conn) {
        this.orderDetailDao = new OrderDetailDaoImpl(conn);
    }

    @Override
    public boolean insert(OrderDetail detail) throws ServiceException {
        try {
            if (!orderDetailDao.insert(detail)) {
                throw new ServiceException("Failed Creating OrderDetail...");
            }
            return true;
        } catch (SQLException e) {
            throw new ServiceException("SQL Found Error (Create Detail): ", e);
        }
    }

    @Override
    public boolean update(OrderDetail detail) throws ServiceException {
        try {
            if (!orderDetailDao.update(detail)) {
                throw new ServiceException("Failed Updating OrderDetail...");
            }
            return true;
        } catch (SQLException e) {
            throw new ServiceException("SQL Found Error (Update Detail): ", e);
        }
    }

    @Override
    public boolean deleteById(int id) throws ServiceException {
        try {
            if (!orderDetailDao.deleteById(id)) {
                throw new ServiceException("Failed Deleting OrderDetail...");
            }
            return true;
        } catch (SQLException e) {
            throw new ServiceException("SQL Found Error (Delete Detail): ", e);
        }
    }

    @Override
    public OrderDetail getById(int id) throws ServiceException {
        try {
            OrderDetail detail = orderDetailDao.getById(id);
            if (detail == null) {
                throw new ServiceException("Order Detail Not Found");
            }
            return detail;
        } catch (SQLException e) {
            throw new ServiceException("Failed Searching OrderDetail...", e);
        }
    }

    @Override
    public List<OrderDetail> getAll() throws ServiceException {
        try {
            return orderDetailDao.getAll();
        } catch (SQLException e) {
            throw new ServiceException("Failed Grabbing OrderDetails...", e);
        }
    }
}