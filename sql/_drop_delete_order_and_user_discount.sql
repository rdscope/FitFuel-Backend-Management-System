-- 清除資料表
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS OrderDetail;
DROP TABLE IF EXISTS `Order`;
DROP TABLE IF EXISTS UserDiscount;
DROP TABLE IF EXISTS UsedDiscountCode;

SET FOREIGN_KEY_CHECKS = 1;

-- Update Threashold
UPDATE User
SET Threshold_accumulated = 0;

-- 清除資料
DELETE FROM OrderDetail;
DELETE FROM `Order`;
DELETE FROM UserDiscount;
DELETE FROM UsedDiscountCode;

-- 清除資料（推薦於測試環境）
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE OrderDetail;
TRUNCATE TABLE `Order`;
TRUNCATE TABLE UserDiscount;
TRUNCATE TABLE UsedDiscountCode;

SET FOREIGN_KEY_CHECKS = 1;

-- DROP CONSTRAINT
ALTER TABLE UsedDiscountCode DROP FOREIGN KEY UsedDiscountCode_ibfk_2;