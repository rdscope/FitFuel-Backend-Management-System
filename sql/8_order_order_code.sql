-- 方法一（推薦）：先加欄位，不設限制 → 再填資料 → 再加 UNIQUE 限制

-- 步驟 1：先加欄位，但允許為 NULL
ALTER TABLE `Order` ADD COLUMN order_code VARCHAR(50);

-- 步驟 2：針對舊資料填上臨時編號（你可以改成真實代碼）
UPDATE `Order` SET order_code = CONCAT('TEMP-', order_id) WHERE order_code IS NULL;

-- 步驟 3：再來加上 NOT NULL + UNIQUE 限制
ALTER TABLE `Order` MODIFY COLUMN order_code VARCHAR(50) NOT NULL UNIQUE;




-- 方法二（如果你是空表）：直接清空再新增欄位

DELETE FROM `Order`; -- 小心！清空所有訂單

ALTER TABLE `Order`
ADD COLUMN order_code VARCHAR(50) NOT NULL UNIQUE;
