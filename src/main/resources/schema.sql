CREATE TABLE IF NOT EXISTS users (
    user_id SERIAL PRIMARY KEY,  
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS Budget (
    budget_id SERIAL PRIMARY KEY, 
    user_id INT NOT NULL, 
    amount DECIMAL(10, 2) NOT NULL, 
    start_date DATE NOT NULL, 
    end_date DATE NOT NULL, 
    CONSTRAINT FK_Budget_User FOREIGN KEY (user_id) REFERENCES users (user_id) 
        ON DELETE CASCADE ON UPDATE CASCADE
);


