CREATE TABLE UsedDiscountCode (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    code VARCHAR(50) NOT NULL,
    used_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    -- UNIQUE(user_id, code)
    UNIQUE KEY unique_user_code (user_id, code)
);