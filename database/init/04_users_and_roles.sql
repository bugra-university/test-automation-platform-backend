-- 04_users_and_roles.sql
-- This file contains tables required for user authentication and authorization

-- Dropping previous tables (if any)
DROP TABLE IF EXISTS user_roles CASCADE;
DROP TABLE IF EXISTS roles CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Roles table - To store user roles
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(20) NOT NULL,
    CONSTRAINT uk_roles_name UNIQUE (name)
);

-- Users table - To store user information
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL,
    password VARCHAR(120) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP,
    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT uk_users_email UNIQUE (email)
);

-- User roles junction table - To store which user has which roles
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);

-- Indexes for users
CREATE INDEX idx_user_username ON users (username);
CREATE INDEX idx_user_email ON users (email);

-- Adding default roles
INSERT INTO roles (name) VALUES ('ROLE_USER');
INSERT INTO roles (name) VALUES ('ROLE_MODERATOR');
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');

-- Adding default admin user (password: admin123)
INSERT INTO users (username, email, password, first_name, last_name)
VALUES ('admin', 'admin@test.com', '$2a$10$eMeAv9YU45TJTLHKNGLcy.X5RIkU0TW.F6Y3dLhkC9vLesvRt0hAy', 'Admin', 'User');

-- Adding test user (password: admin123)
INSERT INTO users (username, email, password, first_name, last_name)
VALUES ('testuser', 'testuser@test.com', '$2a$10$eMeAv9YU45TJTLHKNGLcy.X5RIkU0TW.F6Y3dLhkC9vLesvRt0hAy', 'Test', 'User');

-- Assigning admin role to default users
INSERT INTO user_roles (user_id, role_id)
VALUES 
    (1, 3), -- ROLE_ADMIN for admin user
    (2, 3); -- ROLE_ADMIN for testuser
