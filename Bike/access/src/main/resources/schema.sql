CREATE TABLE parking (
  id INT AUTO_INCREMENT PRIMARY KEY,
  direction VARCHAR(255),
  bikes_capacity INT,
  latitude DOUBLE,
  longitude DOUBLE,
  bikes_available INT,
  free_parking_spots INT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);