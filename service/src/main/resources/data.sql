delete from categories;
delete from users;

ALTER TABLE categories ALTER COLUMN category_id RESTART WITH 1;
ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1;
