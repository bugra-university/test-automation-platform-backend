-- Table creation script

-- Projects table
CREATE TABLE IF NOT EXISTS projects (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Test suites table
CREATE TABLE IF NOT EXISTS test_suites (
    id BIGSERIAL PRIMARY KEY,    project_id BIGINT REFERENCES projects(id),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    folder_path VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Test cases table
CREATE TABLE IF NOT EXISTS test_cases (
    id BIGSERIAL PRIMARY KEY,
    suite_id BIGINT REFERENCES test_suites(id),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    class_name VARCHAR(255) NOT NULL,
    method_name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(class_name, method_name)
);

-- Test runs table
CREATE TABLE IF NOT EXISTS test_runs (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT REFERENCES projects(id),
    name VARCHAR(255),
    start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    end_time TIMESTAMP,
    status VARCHAR(50), -- RUNNING, COMPLETED, FAILED, CANCELLED
    triggered_by VARCHAR(100), -- scheduled, manual, git_push
    environment VARCHAR(50), -- dev, test, prod
    git_commit_hash VARCHAR(100), -- Git commit hash bilgisi
    parameters JSONB, -- Test koşusu parametreleri JSON formatında
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Test results table
CREATE TABLE IF NOT EXISTS test_results (
    id BIGSERIAL PRIMARY KEY,
    test_run_id BIGINT REFERENCES test_runs(id),
    test_case_id BIGINT REFERENCES test_cases(id),
    status VARCHAR(50) NOT NULL, -- PASS, FAIL, SKIP, ERROR
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    duration_ms BIGINT,
    error_message TEXT,
    stack_trace TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(test_run_id, test_case_id)
);

-- Test steps table
CREATE TABLE IF NOT EXISTS test_steps (
    id BIGSERIAL PRIMARY KEY,
    test_result_id BIGINT REFERENCES test_results(id),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL, -- PASS, FAIL, SKIP, INFO
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    duration_ms BIGINT,
    order_number INTEGER
);

-- Screenshots table
CREATE TABLE IF NOT EXISTS screenshots (
    id BIGSERIAL PRIMARY KEY,
    test_result_id BIGINT REFERENCES test_results(id),
    step_id BIGINT REFERENCES test_steps(id),
    file_path VARCHAR(255) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    description TEXT
);

-- Reports table
CREATE TABLE IF NOT EXISTS reports (
    id BIGSERIAL PRIMARY KEY,
    test_run_id BIGINT REFERENCES test_runs(id),
    report_type VARCHAR(50) NOT NULL, -- PDF, HTML, XML
    file_path VARCHAR(255) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Scheduled jobs table
CREATE TABLE IF NOT EXISTS scheduled_jobs (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    cron_expression VARCHAR(100) NOT NULL,
    test_suite_id BIGINT REFERENCES test_suites(id),
    parameters JSONB,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_run TIMESTAMP,
    next_run TIMESTAMP
);

-- Users table (to be integrated with Keycloak)
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    full_name VARCHAR(255),
    keycloak_id VARCHAR(255) UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Roles table
CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User-Roles relationship table
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT REFERENCES users(id),
    role_id BIGINT REFERENCES roles(id),
    PRIMARY KEY (user_id, role_id)
);

-- Create default project
INSERT INTO projects (name, description)
VALUES ('Project Team 09', 'Test Automation Project')
ON CONFLICT DO NOTHING;

-- Default roles
INSERT INTO roles (name, description)
VALUES 
    ('ADMIN', 'System administrator with full access'),
    ('TESTER', 'Can run and view tests'),
    ('DEVELOPER', 'Can view test results'),
    ('VIEWER', 'Can only view dashboards')
ON CONFLICT DO NOTHING;
