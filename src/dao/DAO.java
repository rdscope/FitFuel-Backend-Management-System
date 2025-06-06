package dao;

import java.sql.SQLException;
import java.util.List;

public interface DAO<T> {
    boolean insert(T t) throws SQLException;
    boolean update(T t) throws SQLException;
    boolean deleteById(int id) throws SQLException;

    T getById(int id) throws SQLException;
    List<T> getAll() throws SQLException;
}
