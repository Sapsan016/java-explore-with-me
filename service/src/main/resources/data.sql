delete from categories;
delete from users;
delete from locations;
delete from events;
delete from compilations;
delete from compilation_events;


ALTER TABLE categories ALTER COLUMN category_id RESTART WITH 1;
ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1;
ALTER TABLE locations ALTER COLUMN location_id RESTART WITH 1;
ALTER TABLE events ALTER COLUMN event_id RESTART WITH 1;
ALTER TABLE compilations ALTER COLUMN compilation_id RESTART WITH 1;


