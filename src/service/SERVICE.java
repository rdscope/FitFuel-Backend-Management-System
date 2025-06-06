package service;

import java.sql.SQLException;
import java.util.List;

public interface SERVICE<T> {
    boolean insert(T t) throws ServiceException;
    boolean update(T t) throws ServiceException;
    boolean deleteById(int id) throws ServiceException;

    T getById(int id) throws ServiceException;
    List<T> getAll() throws ServiceException;
}