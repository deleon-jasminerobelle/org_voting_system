-- Create roles table
CREATE TABLE IF NOT EXISTS roles (
    role_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(255) NOT NULL UNIQUE
);

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    birthdate DATE,
    has_voted BIT(1),
    is_active BIT(1),
    created_at DATETIME(6),
    role_id BIGINT,
    email VARCHAR(255) UNIQUE,
    first_name VARCHAR(255),
    full_name VARCHAR(255),
    last_name VARCHAR(255),
    middle_name VARCHAR(255),
    organization VARCHAR(255),
    password_hash VARCHAR(255),
    section VARCHAR(255),
    student_number VARCHAR(255),
    username VARCHAR(255) UNIQUE,
    FOREIGN KEY (role_id) REFERENCES roles(role_id)
);

-- Create elections table
CREATE TABLE IF NOT EXISTS elections (
    election_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at DATETIME(6),
    created_by BIGINT,
    description LONGTEXT,
    end_datetime DATETIME(6),
    start_datetime DATETIME(6),
    status VARCHAR(255),
    title VARCHAR(255),
    FOREIGN KEY (created_by) REFERENCES users(user_id)
);

-- Create positions table
CREATE TABLE IF NOT EXISTS positions (
    position_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    election_id BIGINT,
    position_name VARCHAR(255),
    max_votes INT,
    FOREIGN KEY (election_id) REFERENCES elections(election_id)
);

-- Create candidates table
CREATE TABLE IF NOT EXISTS candidates (
    candidate_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    position_id BIGINT,
    full_name VARCHAR(255),
    description LONGTEXT,
    FOREIGN KEY (position_id) REFERENCES positions(position_id)
);

-- Create votes table
CREATE TABLE IF NOT EXISTS votes (
    vote_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    election_id BIGINT,
    position_id BIGINT,
    candidate_id BIGINT,
    voter_id BIGINT,
    voted_at DATETIME(6),
    FOREIGN KEY (election_id) REFERENCES elections(election_id),
    FOREIGN KEY (position_id) REFERENCES positions(position_id),
    FOREIGN KEY (candidate_id) REFERENCES candidates(candidate_id),
    FOREIGN KEY (voter_id) REFERENCES users(user_id)
);

-- Drop existing view if it exists
DROP VIEW IF EXISTS election_results;

-- Insert roles (ignore if already exist)
INSERT IGNORE INTO roles (role_id, role_name) VALUES (1, 'ROLE_ADMIN');
INSERT IGNORE INTO roles (role_id, role_name) VALUES (2, 'ROLE_ELECTION_OFFICER');
INSERT IGNORE INTO roles (role_id, role_name) VALUES (3, 'ROLE_VOTER');


