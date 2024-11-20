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

CREATE TABLE IF NOT EXISTS Category (
    category_id SERIAL PRIMARY KEY, 
    user_id INT NOT NULL, 
    name VARCHAR(255) NOT NULL, 
    CONSTRAINT FK_Category_User FOREIGN KEY (user_id) REFERENCES users (user_id) 
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS Goal (
    goal_id SERIAL PRIMARY KEY, 
    user_id INT NOT NULL, 
    name VARCHAR(255) NOT NULL, 
    target_amount DECIMAL(10, 2) NOT NULL, 
    current_amount DECIMAL(10, 2) NOT NULL, 
    deadline DATE NOT NULL, 
    status VARCHAR(50) NOT NULL,
    CONSTRAINT FK_Goal_User FOREIGN KEY (user_id) REFERENCES users (user_id) 
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS Transaction (
    transaction_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    category_id INT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    date DATE NOT NULL,  
    description TEXT,
    type VARCHAR(50) NOT NULL,
    CONSTRAINT FK_Transaction_User FOREIGN KEY (user_id) REFERENCES users (user_id) 
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FK_Transaction_Category FOREIGN KEY (category_id) REFERENCES Category (category_id) 
        ON DELETE CASCADE ON UPDATE CASCADE
);



