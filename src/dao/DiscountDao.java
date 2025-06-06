package dao;

import model.Discount;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public interface DiscountDao extends DAO<Discount> {
    Discount getByName(String name) throws SQLException;
    List<Discount> findActiveDiscounts(Date today) throws SQLException;
}
