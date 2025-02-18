SET SESSION cte_max_recursion_depth = 1000000;

DROP TEMPORARY TABLE IF EXISTS temp_numbers;
CREATE TEMPORARY TABLE temp_numbers (n INT PRIMARY KEY);

INSERT INTO temp_numbers (n)
WITH RECURSIVE numbers AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM numbers WHERE n < 1000000
)
SELECT n FROM numbers;

INSERT INTO product (name, price, stock, created_at, updated_at)
SELECT
    CONCAT('Product ', n) AS name,
    FLOOR(RAND() * 1000) + 100 AS price,
    FLOOR(RAND() * 1000) + 1 AS stock,
    NOW() AS created_at,
    NOW() AS updated_at
FROM temp_numbers
WHERE n <= 100000;

INSERT INTO order_item (order_id, product_id, product_name, price, quantity, created_at, updated_at)
SELECT
    n AS order_id,
    FLOOR(RAND() * 100000) + 1 AS product_id,
    CONCAT('Product ', FLOOR(RAND() * 100000) + 1) AS product_name,
    FLOOR(RAND() * 1000) + 100 AS price,
    FLOOR(RAND() * 10) + 1 AS quantity,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 10) DAY) AS created_at,
    NOW() AS updated_at
FROM temp_numbers;

DROP TEMPORARY TABLE IF EXISTS temp_numbers;
