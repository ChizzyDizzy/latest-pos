-- SQL Script to add CUSTOMER role support to existing database
-- Run this with: mysql -u root -p syos_db < add_customer_role_support.sql

USE syos_db;

-- Step 1: Alter the users table to add CUSTOMER to the role ENUM
ALTER TABLE users MODIFY COLUMN role ENUM('ADMIN', 'CASHIER', 'MANAGER', 'CUSTOMER') NOT NULL;

-- Step 2: Insert a default customer user if it doesn't exist
-- Password: customer123 (SHA-256 hash: 6ed0b4dcab61c966e7f5aea5a9ea4c2cfbf28c41e5c7e0daaa29de2d5dafee11)
INSERT INTO users (username, email, password_hash, role)
SELECT 'customer1', 'customer1@syos.com', '6ed0b4dcab61c966e7f5aea5a9ea4c2cfbf28c41e5c7e0daaa29de2d5dafee11', 'CUSTOMER'
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE username = 'customer1'
);

-- Verification: Show all users with their roles
SELECT
    username,
    email,
    role,
    CASE username
        WHEN 'admin' THEN 'admin123'
        WHEN 'cashier1' THEN 'cashier123'
        WHEN 'manager1' THEN 'manager123'
        WHEN 'customer1' THEN 'customer123'
        ELSE 'N/A'
    END as password,
    created_at,
    last_login_at
FROM users
ORDER BY
    FIELD(role, 'ADMIN', 'MANAGER', 'CASHIER', 'CUSTOMER'),
    username;

-- Display confirmation
SELECT '✓ CUSTOMER role successfully added to the database!' as Status;
SELECT COUNT(*) as total_users FROM users;
SELECT role, COUNT(*) as count FROM users GROUP BY role ORDER BY FIELD(role, 'ADMIN', 'MANAGER', 'CASHIER', 'CUSTOMER');
