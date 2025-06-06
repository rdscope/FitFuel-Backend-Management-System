-- 增加 name 欄位
ALTER TABLE Discount
ADD COLUMN name VARCHAR(100) NOT NULL AFTER discount_id;

-- 修改 discount_type 欄位
ALTER TABLE Discount
ADD COLUMN discount_type ENUM('percent', 'fixed') NOT NULL AFTER name;

-- 檢查 amount 是否為 DECIMAL(10,2)
ALTER TABLE Discount
MODIFY COLUMN amount DECIMAL(10,2) NOT NULL;