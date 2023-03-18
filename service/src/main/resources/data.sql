delete from categories;

ALTER TABLE categories ALTER COLUMN category_id RESTART WITH 1;
