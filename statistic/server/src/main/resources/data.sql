delete from hits;

ALTER TABLE hits ALTER COLUMN hit_id RESTART WITH 1;
