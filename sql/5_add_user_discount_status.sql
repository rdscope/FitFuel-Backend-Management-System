ALTER TABLE UserDiscount
ADD COLUMN status ENUM('Unused', 'Used') DEFAULT 'Unused';