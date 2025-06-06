package service;

import service.ServiceException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AuditLogServiceImpl {
    private final Connection conn;

    public AuditLogServiceImpl(Connection conn) {
        this.conn = conn;
    }

    public void log(String tableName, String action, String description, int userId) throws ServiceException {
        String sql = "INSERT INTO AuditLog (table_name, action, description, user_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tableName);
            stmt.setString(2, action);
            stmt.setString(3, description);
            stmt.setInt(4, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new ServiceException("Failed to insert audit log", e);
        }
    }
}
