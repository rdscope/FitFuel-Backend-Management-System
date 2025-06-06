-- FitFuel Fitness Food Database Schema (Second Edition)
CREATE SCHEMA IF NOT EXISTS fitfuel_fitness_food_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE fitfuel_fitness_food_db;

-- User table with role
CREATE TABLE User(
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    birthday DATE NOT NULL,
    register_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    role VARCHAR(20) NOT NULL DEFAULT 'user'
);

-- Discount table
CREATE TABLE IF NOT EXISTS Discount (
    discount_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50),                            -- 折扣碼（可為 NULL）
    discount_amount DECIMAL(10,2) NOT NULL,      -- 折扣數值
    is_percentage BOOLEAN NOT NULL,              -- true=百分比，false=固定金額
    is_single_use BOOLEAN DEFAULT FALSE,         -- 是否限單次使用
    is_recurring BOOLEAN DEFAULT FALSE,          -- 是否為每年定期活動
    recurring_start_month INT,                   -- 每年開始月（僅 recurring）
    recurring_start_day INT,
    recurring_end_month INT,
    recurring_end_day INT,
    start_date DATE,                             -- 一次性活動用（與 recurring 擇一）
    end_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- UserDiscount table
CREATE TABLE IF NOT EXISTS UserDiscount (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,                           -- 使用者 ID
    discount_id INT NOT NULL,                       -- 對應 Discount 主檔
    start_date DATE NOT NULL,                       -- 優惠券啟用時間
    end_date DATE NOT NULL,                         -- 優惠券有效期限
    status VARCHAR(20) NOT NULL DEFAULT 'Unused',   -- 狀態：Unused / Used
    issued_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES User(user_id),
    FOREIGN KEY (discount_id) REFERENCES Discount(discount_id)
);


-- Category table
CREATE TABLE Category(
    category_id INT PRIMARY KEY AUTO_INCREMENT,
    category_name VARCHAR(100) NOT NULL UNIQUE
);

-- Product table
CREATE TABLE Product(
    product_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    category_id INT,
    price DECIMAL(10,2) NOT NULL,
    stock_quantity INT DEFAULT 0,
    FOREIGN KEY (category_id) REFERENCES Category(category_id)
);

-- RawMaterial table
CREATE TABLE RawMaterial(
    raw_material_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    supplier VARCHAR(100),
    stock_quantity DECIMAL(10,2),
    low_stock_threshold DECIMAL(10,2) DEFAULT 0
);

-- ProductMaterial table
CREATE TABLE ProductMaterial(
    product_id INT,
    raw_material_id INT,
    quantity_per_product DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (product_id, raw_material_id),
    FOREIGN KEY (product_id) REFERENCES Product(product_id),
    FOREIGN KEY (raw_material_id) REFERENCES RawMaterial(raw_material_id)
);

-- Order table with DATETIME
CREATE TABLE `Order`(
    order_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    order_date DATETIME NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT '已下單',
    total_amount DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES User(user_id)
);

-- OrderDetail table
CREATE TABLE OrderDetail(
    order_detail_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT,
    product_id INT,
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES `Order`(order_id),
    FOREIGN KEY (product_id) REFERENCES Product(product_id)
);

-- UsedDiscountCode table (from 5_create_used_discount_code.sql)
CREATE TABLE IF NOT EXISTS UsedDiscountCode (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    code VARCHAR(50) NOT NULL,
    used_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES User(user_id)
);

-- Initial categories (from 2_add_category_table.sql)
INSERT INTO Category (category_name) VALUES
('Protein Shakes'),         -- 蛋白飲品
('Fitness Meal Boxes'),     -- 健身餐盒
('Cold-Pressed Juices'),    -- 蔬果冷壓汁
('Energy Bars'),            -- 健康能量棒
('Vegan Meals'),            -- 純素餐點
('Low-Carb Options'),       -- 低碳飲食
('Limited Editions'),       -- 限時商品
('Supplements');            -- 營養補給品
