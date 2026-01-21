-- Schema for Organization Voting System

-- Roles table
CREATE TABLE IF NOT EXISTS roles (
    role_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_name ENUM('ROLE_ADMIN', 'ROLE_VOTER', 'ROLE_ELECTION_OFFICER') NOT NULL UNIQUE
);

-- Users table
CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50),
    middle_name VARCHAR(50),
    last_name VARCHAR(50),
    full_name VARCHAR(255),
    student_number VARCHAR(20) UNIQUE,
    is_active BOOLEAN DEFAULT TRUE,
    has_voted BOOLEAN DEFAULT FALSE,
    role_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(role_id)
);

-- Elections table
CREATE TABLE IF NOT EXISTS elections (
    election_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    start_datetime TIMESTAMP NOT NULL,
    end_datetime TIMESTAMP NOT NULL,
    status ENUM('UPCOMING', 'ACTIVE', 'CLOSED') DEFAULT 'UPCOMING',
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(user_id)
);

-- Positions table
CREATE TABLE IF NOT EXISTS positions (
    position_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    election_id BIGINT NOT NULL,
    position_name VARCHAR(255) NOT NULL,
    max_votes INT DEFAULT 1,
    FOREIGN KEY (election_id) REFERENCES elections(election_id)
);

-- Candidates table
CREATE TABLE IF NOT EXISTS candidates (
    candidate_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    position_id BIGINT NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    description TEXT,
    FOREIGN KEY (position_id) REFERENCES positions(position_id)
);

-- Votes table
CREATE TABLE IF NOT EXISTS votes (
    vote_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    candidate_id BIGINT NOT NULL,
    position_id BIGINT NOT NULL,
    election_id BIGINT NOT NULL,
    voted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (candidate_id) REFERENCES candidates(candidate_id),
    FOREIGN KEY (position_id) REFERENCES positions(position_id),
    FOREIGN KEY (election_id) REFERENCES elections(election_id),
    UNIQUE KEY unique_vote (user_id, position_id, election_id)
);
