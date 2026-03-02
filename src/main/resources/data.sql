-- PostgreSQL compatible data initialization script
-- This script uses PostgreSQL-specific features for optimal performance

-- Insert roles (PostgreSQL will auto-generate IDs using IDENTITY)
INSERT INTO role (role_id, role) VALUES 
    (1, 'ROLE_ADMIN'), 
    (2, 'ROLE_USER'), 
    (3, 'ROLE_MANAGER'), 
    (4, 'ROLE_OWNER');

-- Reset sequence for role table to avoid conflicts
SELECT setval('role_role_id_seq', (SELECT MAX(role_id) FROM role));

-- Insert users with bcrypt-encoded passwords
-- admin - pass = admin
-- user - pass = user
-- manager - pass = manager
-- owner - pass = owner
INSERT INTO users (id, email, enabled, first_name, last_name, password, username, role_role_id)
VALUES 
    (1, 'a@u', 1, 'AFN', 'ALN', '$2a$10$iPgnenFIoM67cYL9let/iOLBphbDaEkAz3BmiXOCmWq5A4M2TkXAG', 'admin', 1),
    (2, 'u@m', 1, 'UFN', 'ULN', '$2a$10$Ad.n7DA3e9QT.a8hXymxI.JKnAYTLR4nD4stJtfMiCLcr7FiZ/st.', 'user', 2),
    (3, 'm@m', 1, 'MFN', 'MLN', '$2a$10$iQy1MYc97kkXBwrCJ5I9gO/QcRT.rdY6UDKriBvG.iyX29miDaKDe', 'manager', 3),
    (4, 'o@m', 1, 'OFN', 'OLN', '$2a$10$VVH6bnOWLMczmH12BY99c.T6JMzMErt/gZKRCPfYlXcq7JMFoqkWW', 'owner', 4);

-- Reset sequence for users table
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));

-- Insert categories
INSERT INTO category (category_id, category)
VALUES 
    (1, 'small'), 
    (2, 'medium'), 
    (3, 'big');

-- Reset sequence for category table
SELECT setval('category_category_id_seq', (SELECT MAX(category_id) FROM category));

-- Insert customers
INSERT INTO customer (id, address, city, email, enabled, first_name, last_name, name, phone)
VALUES 
    (1, 'Small Street', 'Smallville', 'smallmail@mail.com', 1, 'SmallFN', 'SmallLN', 'Small INC', 123),
    (2, 'Medium Street', 'Midtown', 'midmail@mail.com', 1, 'MidFN', 'MidLN', 'Mid INC', 456),
    (3, 'Big Street', 'Big City', 'bigmail@mail.com', 1, 'BigFN', 'BigLN', 'Big INC', 789);

-- Reset sequence for customer table
SELECT setval('customer_id_seq', (SELECT MAX(id) FROM customer));

-- Insert customer-category relationships
INSERT INTO customer_category (customer_id, category_id)
VALUES 
    (1, 1), 
    (2, 2), 
    (3, 3);

-- Insert contracts using PostgreSQL date casting
INSERT INTO contract (id, begin_date, content, end_date, name, status, value, customer_id, user_id)
VALUES (1, '2018-02-24'::date, 'contract content', '2018-02-25'::date, 'ContractName', 'PROPOSED', 100000.00, 2, 2);

-- Reset sequence for contract table
SELECT setval('contract_id_seq', (SELECT MAX(id) FROM contract));

-- Create indexes for better query performance (if not already created by JPA)
CREATE INDEX IF NOT EXISTS idx_customer_enabled_name ON customer(enabled, name);
CREATE INDEX IF NOT EXISTS idx_contract_customer_user ON contract(customer_id, user_id);
CREATE INDEX IF NOT EXISTS idx_users_username_enabled ON users(username, enabled);

-- Analyze tables for query optimization
ANALYZE role;
ANALYZE users;
ANALYZE category;
ANALYZE customer;
ANALYZE customer_category;
ANALYZE contract;
