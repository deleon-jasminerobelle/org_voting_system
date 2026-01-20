-- Drop existing view if it exists
DROP VIEW IF EXISTS election_results;

-- Insert roles
INSERT INTO roles (role_id, role_name) VALUES (1, 'ADMIN');
INSERT INTO roles (role_id, role_name) VALUES (2, 'ELECTION_OFFICER');
INSERT INTO roles (role_id, role_name) VALUES (3, 'VOTER');

-- Insert users
INSERT INTO users (birthdate, has_voted, is_active, created_at, role_id, user_id, email, first_name, full_name, last_name, middle_name, organization, password_hash, section, student_number, username) VALUES
('1990-01-01', b'0', b'1', '2026-01-20 11:34:22.000000', 1, 1, 'admin@example.com', 'Admin', 'Admin User', 'User', NULL, 'Organization', '$2a$10$ffrHc9bj3A/S6Zt9C9EpFuLqkclt5QQuLZSozYyxVkn2Nx43sUTO.', 'Admin', 'ADMIN001', 'admin'),
('1990-01-01', b'0', b'1', '2026-01-20 11:34:22.000000', 2, 2, 'eo@example.com', 'Election', 'Election Officer', 'Officer', NULL, 'Organization', '$2a$10$7y24ELP6hpX3jnu00N68fuNWHCOvVZPSKM4d16jHlZ8XkmOP9NXZ6', 'EO', 'EO001', 'eo'),
('1995-05-15', b'0', b'1', '2026-01-20 11:34:22.000000', 3, 3, 'voter@example.com', 'John', 'John Doe', 'Doe', NULL, 'Organization', '$2a$10$KQT5rS4Xr.IWgiZmC9ENFeyvfTUFCdZ3Y/MMEC8hfFiSjq1TmWQ/a', 'Voter', 'VOTER001', 'voter');
