DELETE FROM comments;
ALTER TABLE comments ALTER COLUMN id RESTART WITH 1;
DELETE FROM bookings;
ALTER TABLE bookings ALTER COLUMN id RESTART WITH 1;
DELETE FROM items;
ALTER TABLE items ALTER COLUMN id RESTART WITH 1;
DELETE FROM requests;
ALTER TABLE requests ALTER COLUMN id RESTART WITH 1;
DELETE FROM users;
ALTER TABLE users ALTER COLUMN id RESTART WITH 1;