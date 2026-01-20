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

-- Insert users (ignore if already exist)
INSERT IGNORE INTO users (birthdate, has_voted, is_active, created_at, role_id, user_id, email, first_name, full_name, last_name, middle_name, organization, password_hash, section, student_number, username) VALUES
('1990-01-01', b'0', b'1', '2026-01-20 11:34:22.000000', 1, 1, 'admin@example.com', 'Admin', 'Admin User', 'User', NULL, 'Organization', '$2a$10$ffrHc9bj3A/S6Zt9C9EpFuLqkclt5QQuLZSozYyxVkn2Nx43sUTO.', 'Admin', 'ADMIN001', 'admin'),
('1990-01-01', b'0', b'1', '2026-01-20 11:34:22.000000', 2, 2, 'eo@example.com', 'Election', 'Election Officer', 'Officer', NULL, 'Organization', '$2a$10$7y24ELP6hpX3jnu00N68fuNWHCOvVZPSKM4d16jHlZ8XkmOP9NXZ6', 'EO', 'EO001', 'eo'),
('1995-05-15', b'0', b'1', '2026-01-20 11:34:22.000000', 3, 3, 'voter@example.com', 'John', 'John Doe', 'Doe', NULL, 'Organization', '$2a$10$KQT5rS4Xr.IWgiZmC9ENFeyvfTUFCdZ3Y/MMEC8hfFiSjq1TmWQ/a', 'Voter', 'VOTER001', 'voter');
