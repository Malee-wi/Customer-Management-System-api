USE customer_db;

-- Insert sample customers
INSERT INTO customer (id, name, dob, nic) VALUES
                                              (1, 'John Doe', '1990-05-15', '199005150123'),
                                              (2, 'Jane Smith', '1988-08-22', '198808220456'),
                                              (3, 'Mike Johnson', '1995-03-10', '19950310789'),
                                              (4, 'Sarah Williams', '1992-11-30', '199211301234');

ALTER TABLE customer AUTO_INCREMENT = 5;

-- Insert sample mobile numbers
INSERT INTO mobile_number (number, customer_id) VALUES
                                                    ('+94771234567', 1),
                                                    ('+94772345678', 1),
                                                    ('+94773456789', 2),
                                                    ('+94774567890', 3);

-- Insert sample addresses
INSERT INTO address (line1, line2, city, country, customer_id) VALUES
                                                                   ('123 Main Street', 'Apt 4B', 'Colombo', 'Sri Lanka', 1),
                                                                   ('456 Park Avenue', NULL, 'Kandy', 'Sri Lanka', 1),
                                                                   ('789 Broadway', 'Suite 100', 'New York', 'United States', 2);