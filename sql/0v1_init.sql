CREATE SCHEMA IF NOT EXISTS fitfuel_fitness_food_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE fitfuel_fitness_food_db;


CREATE TABLE User(
user_id INT PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(100) NOT NULL,
email VARCHAR(100) UNIQUE NOT NULL,
password VARCHAR(100) NOT NULL,
birthday DATE,
register_date DATE
);


CREATE TABLE Discount(
discount_id INT PRIMARY KEY AUTO_INCREMENT,
discount_type VARCHAR(50) NOT NULL,
amount DECIMAL(5,2) NOT NULL,
start_date DATE,
end_date DATE
);


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


CREATE TABLE Category(
category_id INT PRIMARY KEY AUTO_INCREMENT,
category_name VARCHAR(100) NOT NULL
);


CREATE TABLE Product(
product_id INT PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(100) NOT NULL,
category_id INT,
price DECIMAL(10,2) NOT NULL,
stock_quantity INT DEFAULT 0,
FOREIGN KEY (category_id) REFERENCES Category(category_id)
);


CREATE TABLE RawMaterial(
raw_material_id INT PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(100) NOT NULL,
supplier VARCHAR(100),
stock_quantity INT DEFAULT 0
);


CREATE TABLE ProductMaterial(
product_id INT,
raw_material_id INT,
quantity_per_product INT NOT NULL,
PRIMARY KEY (product_id, raw_material_id),
FOREIGN KEY (product_id) REFERENCES Product(product_id),
FOREIGN KEY (raw_material_id) REFERENCES RawMaterial(raw_material_id)
);


CREATE TABLE `Order`(
order_id INT PRIMARY KEY AUTO_INCREMENT,
user_id INT,
order_date DATE,
status VARCHAR(50),
total_amount DECIMAL(10,2),
FOREIGN KEY (user_id) REFERENCES User(user_id)
);


CREATE TABLE OrderDetail(
order_detail_id INT PRIMARY KEY AUTO_INCREMENT,
order_id INT,
product_id INT,
quantity INT NOT NULL,
price DECIMAL(10,2) NOT NULL,
FOREIGN KEY (order_id) REFERENCES `Order`(order_id),
FOREIGN KEY (product_id) REFERENCES Product(product_id)
);