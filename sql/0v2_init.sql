CREATE SCHEMA IF NOT EXISTS fitfuel_fitness_food_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE fitfuel_fitness_food_db;

-- 初始化 Category
CREATE TABLE IF NOT EXISTS Category (
    category_id INT PRIMARY KEY AUTO_INCREMENT,
    category_name VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO Category (category_name) VALUES
('Protein Shakes'),         -- 蛋白飲品
('Fitness Meal Boxes'),     -- 健身餐盒
('Cold-Pressed Juices'),    -- 蔬果冷壓汁
('Energy Bars'),            -- 健康能量棒
('Vegan Meals'),            -- 純素餐點
('Low-Carb Options'),       -- 低碳飲食
('Limited Editions'),       -- 限時商品
('Supplements');            -- 營養補給品

-- 初始化 Product
CREATE TABLE IF NOT EXISTS Product (
    product_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    category_id INT,
    price DECIMAL(10, 2) NOT NULL,
    stock_quantity INT NOT NULL,
    is_made_to_order BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (category_id) REFERENCES Category(category_id)
);

-- 初始化 RawMaterial
CREATE TABLE IF NOT EXISTS RawMaterial (
    raw_material_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    supplier VARCHAR(100),
    stock_quantity INT NOT NULL
);

-- 初始化 ProductMaterial
CREATE TABLE IF NOT EXISTS ProductMaterial (
    product_id INT,
    raw_material_id INT,
    quantity_per_product INT NOT NULL,
    PRIMARY KEY (product_id, raw_material_id),
    FOREIGN KEY (product_id) REFERENCES Product(product_id),
    FOREIGN KEY (raw_material_id) REFERENCES RawMaterial(raw_material_id)
);

-- 初始化 User
CREATE TABLE IF NOT EXISTS User (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL
);

-- 初始化 Discount
CREATE TABLE IF NOT EXISTS Discount (
    discount_id INT PRIMARY KEY AUTO_INCREMENT,
    discount_name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    amount DECIMAL(10, 2) NOT NULL
);

-- 初始化 UserDiscount
CREATE TABLE IF NOT EXISTS UserDiscount (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    discount_id INT,
    amount DECIMAL(10, 2),
    start_date DATE,
    end_date DATE,
    FOREIGN KEY (user_id) REFERENCES User(user_id),
    FOREIGN KEY (discount_id) REFERENCES Discount(discount_id)
);

-- 初始化 Order
CREATE TABLE IF NOT EXISTS `Order` (
    order_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50),
    total_amount DECIMAL(10, 2),
    FOREIGN KEY (user_id) REFERENCES User(user_id)
);

-- 初始化 OrderDetail
CREATE TABLE IF NOT EXISTS OrderDetail (
    order_detail_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT,
    product_id INT,
    quantity INT,
    price DECIMAL(10, 2),
    FOREIGN KEY (order_id) REFERENCES `Order`(order_id),
    FOREIGN KEY (product_id) REFERENCES Product(product_id)
);