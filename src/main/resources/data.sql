-- Do not load this file in production environments
-- This file should only be used in development/test environments

-- Role initialization (development only)
INSERT INTO role (role_id, role) VALUES
(1, 'ROLE_ADMIN'),
(2, 'ROLE_USER'),
(3, 'ROLE_MANAGER'),
(4, 'ROLE_OWNER');

-- Sample category data for development
SET FOREIGN_KEY_CHECKS=0;
INSERT INTO category (category_id, category)
VALUES
('1', 'small'),
('2', 'medium'),
('3', 'big');

-- Sample customer data for development
INSERT INTO customer (id, address, city, email, enabled, first_name, last_name, name, phone)
VALUES
('1', 'Small Street', 'Smallville', 'sample1@example.com', '1', 'SmallFN', 'SmallLN', 'Small INC', '555-123-4567'),
('2', 'Medium Street', 'Midtown', 'sample2@example.com', '1', 'MidFN', 'MidLN', 'Mid INC', '555-234-5678'),
('3', 'Big Street', 'Big City', 'sample3@example.com', '1', 'BigFN', 'BigLN', 'Big INC', '555-345-6789');

-- Customer category relationships
INSERT INTO customer_category (customer_id, category_id)
VALUES (1, 1), (2, 2), (3, 3);

-- Sample contract for development
INSERT INTO contract (id, begin_date, content, end_date, name, status, value, customer_id, user_id)
VALUES ('1', '2018-02-24 00:00:00', 'Sample contract content', '2018-02-25 00:00:00', 'Sample Contract', 'PROPOSED', '100000.00', '2', '2');

SET FOREIGN_KEY_CHECKS=1;