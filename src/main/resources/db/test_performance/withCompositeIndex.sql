DROP INDEX meal_idx;

CREATE INDEX meal_idx ON meal (user_id, date_time);

EXPLAIN ANALYZE
SELECT *
FROM meal
WHERE user_id = 100000
  AND date_time BETWEEN '2015-02-10' AND '2015-05-20'
ORDER BY date_time DESC;

EXPLAIN ANALYZE
SELECT *
FROM meal
WHERE user_id = 100003
ORDER BY date_time DESC;