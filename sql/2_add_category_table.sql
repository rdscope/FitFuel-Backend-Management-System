-- 2_add_category_table.sql
-- 目的：新增 Category 表與補上 Product 表 category_id 外鍵

-- 1. 建立 Category 表
CREATE TABLE IF NOT EXISTS Category (
    category_id INT PRIMARY KEY AUTO_INCREMENT,
    category_name VARCHAR(50) NOT NULL UNIQUE
);

-- 2. 預設插入基本分類
INSERT INTO Category (category_name) VALUES
('Protein Shakes'),         -- 蛋白飲品
('Fitness Meal Boxes'),     -- 健身餐盒
('Cold-Pressed Juices'),    -- 蔬果冷壓汁
('Energy Bars'),            -- 健康能量棒
('Vegan Meals'),            -- 純素餐點
('Low-Carb Options'),       -- 低碳飲食
('Limited Editions'),       -- 限時商品
('Supplements');            -- 營養補給品

-- 3. 修改 Product 表 (若已有 category_id 欄位可忽略，只做確認)
ALTER TABLE Product
ADD COLUMN IF NOT EXISTS category_id INT,
ADD CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES Category(category_id);


