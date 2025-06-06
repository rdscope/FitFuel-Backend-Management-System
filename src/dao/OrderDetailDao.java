package dao;

import model.OrderDetail;

import java.sql.SQLException;
import java.util.List;

public interface OrderDetailDao extends DAO<OrderDetail> {
    List<OrderDetail> findByOrder(int orderId) throws SQLException;

    boolean updateRemark(int orderDetailId, String remark) throws SQLException;
}
