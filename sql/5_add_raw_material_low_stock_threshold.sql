ALTER TABLE RawMaterial ADD COLUMN low_stock_threshold DECIMAL(10,2) DEFAULT 0;

ALTER TABLE RawMaterial
MODIFY stock_quantity DECIMAL(10,2),
MODIFY low_stock_threshold DECIMAL(10,2);

ALTER TABLE ProductMaterial MODIFY quantity_per_product DECIMAL(10,2);