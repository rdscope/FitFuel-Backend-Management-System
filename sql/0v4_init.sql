
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
    register_date DATE NOT NULL DEFAULT CURRENT_DATE,
    role VARCHAR(20) NOT NULL DEFAULT 'user'
);

-- Discount table
CREATE TABLE discount (
    discount_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    discount_type ENUM('percent', 'fixed') NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL
);

-- UserDiscount table
CREATE TABLE UserDiscount(
    user_id INT,
    discount_id INT,
    amount DECIMAL(5,2),
    start_date DATE,
    end_date DATE,
    PRIMARY KEY (user_id, discount_id),
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
    stock_quantity INT DEFAULT 0
);

-- ProductMaterial table
CREATE TABLE ProductMaterial(
    product_id INT,
    raw_material_id INT,
    quantity_per_product INT NOT NULL,
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
