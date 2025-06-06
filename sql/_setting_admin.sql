-- Method 01
INSERT INTO User (name, email, password, birthday, created_date, role)
VALUES ('Admin', 'admin@example.com', 'admin123', '1990-01-01', NOW(), 'admin');
-- Method 02
UPDATE User SET role = 'admin' WHERE email = 'jeff@example.com';
-- Method 03
userService.insert(new User(0, "Admin", "admin@example.com", "admin123", Date.valueOf("1990-01-01"), new Date(System.currentTimeMillis()), "admin"));
