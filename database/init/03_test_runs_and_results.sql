-- 03_test_runs_and_results.sql
-- This file contains tables related to test runs and results

-- Dropping previous tables (if any)
DROP TABLE IF EXISTS screenshots CASCADE;
DROP TABLE IF EXISTS test_results CASCADE;
DROP TABLE IF EXISTS test_runs CASCADE;

-- Test Runs table - To store test runs
CREATE TABLE test_runs (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL,
    name VARCHAR(255),
    start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    end_time TIMESTAMP,
    status VARCHAR(50), -- RUNNING, COMPLETED, FAILED, CANCELLED
    triggered_by VARCHAR(100), -- scheduled, manual, git_push
    environment VARCHAR(50), -- dev, test, prod
    parameters JSONB, -- Test run parameters in JSON format
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
);

-- Indexes for test runs
CREATE INDEX idx_testrun_project_id ON test_runs (project_id);
CREATE INDEX idx_testrun_status ON test_runs (status);
CREATE INDEX idx_testrun_start_time ON test_runs (start_time);

-- Test Results table - To store test results
CREATE TABLE test_results (
    id BIGSERIAL PRIMARY KEY,
    test_run_id BIGINT NOT NULL,
    test_case_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL, -- PASS, FAIL, SKIP, ERROR
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    duration_ms BIGINT,
    error_message TEXT,
    stack_trace TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (test_run_id) REFERENCES test_runs(id) ON DELETE CASCADE,
    FOREIGN KEY (test_case_id) REFERENCES test_cases(id) ON DELETE CASCADE
);

-- Indexes for test results
CREATE INDEX idx_testresult_test_run_id ON test_results (test_run_id);
CREATE INDEX idx_testresult_test_case_id ON test_results (test_case_id);
CREATE INDEX idx_testresult_status ON test_results (status);

-- Screenshots table - To store screenshots
CREATE TABLE screenshots (
    id BIGSERIAL PRIMARY KEY,
    test_result_id BIGINT NOT NULL,
    file_path VARCHAR(255) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    description TEXT,
    step_number INT, -- Related test step number (optional)
    FOREIGN KEY (test_result_id) REFERENCES test_results(id) ON DELETE CASCADE
);

-- Indexes for screenshots  
CREATE INDEX idx_screenshot_test_result_id ON screenshots (test_result_id);

-- Test Execution Metadata table - To track which test case is running
CREATE TABLE test_execution_metadata (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL,
    user_story_id VARCHAR(10) NOT NULL,
    test_case_id VARCHAR(10) NOT NULL,
    execution_time TIMESTAMP NOT NULL,
    report_file_name VARCHAR(255),
    report_file_path VARCHAR(500),
    status VARCHAR(20) DEFAULT 'RUNNING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
);

-- Indexes for test execution metadata
CREATE INDEX idx_test_execution_project_id ON test_execution_metadata (project_id);
CREATE INDEX idx_test_execution_time ON test_execution_metadata (execution_time);
CREATE INDEX idx_test_execution_report_file ON test_execution_metadata (report_file_name);
