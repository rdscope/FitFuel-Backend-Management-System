CREATE TABLE IF NOT EXISTS AuditLog (
    audit_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    table_name VARCHAR(50) NOT NULL,
    action VARCHAR(20) NOT NULL,         -- CREATE / READ / UPDATE / DELETE
    description TEXT,
    operation_type VARCHAR(50) DEFAULT 'manual', -- 可選：manual / system / api
    timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES User(user_id)
);