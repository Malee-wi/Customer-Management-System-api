-- Create Database
CREATE DATABASE IF NOT EXISTS customer_db;
USE customer_db;

-- Customers Table
CREATE TABLE IF NOT EXISTS customer (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        name VARCHAR(200) NOT NULL,
                                        dob DATE NOT NULL,
                                        nic VARCHAR(20) NOT NULL UNIQUE,
                                        INDEX idx_nic (nic),
                                        INDEX idx_name (name)
);

-- Mobile Numbers Table
CREATE TABLE IF NOT EXISTS mobile_number (
                                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             number VARCHAR(20) NOT NULL,
                                             customer_id BIGINT NOT NULL,
                                             FOREIGN KEY (customer_id) REFERENCES customer(id) ON DELETE CASCADE,
                                             INDEX idx_customer_id (customer_id),
                                             INDEX idx_number (number)
);

-- Addresses Table
CREATE TABLE IF NOT EXISTS address (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       line1 VARCHAR(255) NOT NULL,
                                       line2 VARCHAR(255),
                                       city VARCHAR(100) NOT NULL,
                                       country VARCHAR(100) NOT NULL,
                                       customer_id BIGINT NOT NULL,
                                       FOREIGN KEY (customer_id) REFERENCES customer(id) ON DELETE CASCADE,
                                       INDEX idx_customer_id (customer_id),
                                       INDEX idx_city (city),
                                       INDEX idx_country (country)
);