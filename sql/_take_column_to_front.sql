-- 改資料表結構 → 把欄位物理順序「移動到最前面」
ALTER TABLE `Order`
MODIFY COLUMN order_code VARCHAR(50) FIRST;