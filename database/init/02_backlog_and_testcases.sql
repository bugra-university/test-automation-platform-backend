-- 02_backlog_and_testcases.sql
-- This file contains tables related to product backlog and test cases

-- Dropping previous tables (if any)
DROP TABLE IF EXISTS backlog_test_case_relation CASCADE;
DROP TABLE IF EXISTS test_steps CASCADE;
DROP TABLE IF EXISTS test_cases CASCADE;
DROP TABLE IF EXISTS product_backlog_items CASCADE;
DROP TABLE IF EXISTS test_case_excel_mapping CASCADE;

-- Product Backlog Items table - To store product backlog items
CREATE TABLE product_backlog_items (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL,
    excel_sheet_id BIGINT NOT NULL,
    user_story_id VARCHAR(50) NOT NULL, -- e.g. US_01, US_02
    description TEXT,
    team VARCHAR(255),
    acceptance_criteria TEXT,
    home_quality VARCHAR(100), -- e.g. High Quality
    validation_type VARCHAR(50), -- e.g. UI
    row_index INT, -- Row position in Excel
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    FOREIGN KEY (excel_sheet_id) REFERENCES excel_sheets(id) ON DELETE CASCADE
);

-- Indexes for backlog items
CREATE INDEX idx_backlog_project_id ON product_backlog_items (project_id);
CREATE INDEX idx_backlog_sheet_id ON product_backlog_items (excel_sheet_id);
CREATE INDEX idx_backlog_user_story_id ON product_backlog_items (user_story_id);

-- Test Cases table - To store test scenarios
CREATE TABLE test_cases (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL,
    excel_sheet_id BIGINT NOT NULL,
    user_story_id VARCHAR(50) NOT NULL, -- e.g. US01
    test_case_id VARCHAR(50) NOT NULL, -- e.g. TC01
    test_objective TEXT NOT NULL, -- e.g. "Sign up when all areas are filled"
    pre_condition TEXT, -- e.g. "Access to the Site"
    notes TEXT, -- Notes or BUG descriptions
    status VARCHAR(50) DEFAULT 'ACTIVE', -- ACTIVE, DEPRECATED
    scenario_type VARCHAR(20), -- Positive/Negative scenario
    bug_notes TEXT[], -- BUG notes as array
    row_index INT, -- Start row in Excel
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    FOREIGN KEY (excel_sheet_id) REFERENCES excel_sheets(id) ON DELETE CASCADE,
    UNIQUE(project_id, user_story_id, test_case_id) -- Unique user_story + test_case ID combination per project
);

-- Indexes for test cases
CREATE INDEX idx_testcase_project_id ON test_cases (project_id);
CREATE INDEX idx_testcase_sheet_id ON test_cases (excel_sheet_id);
CREATE INDEX idx_testcase_user_story_id ON test_cases (user_story_id);
CREATE INDEX idx_testcase_test_case_id ON test_cases (test_case_id);

-- Test Steps table - To store test steps
CREATE TABLE test_steps (
    id BIGSERIAL PRIMARY KEY,
    test_case_id BIGINT NOT NULL,
    step_number INT NOT NULL, -- No (sequence number)
    step_description TEXT NOT NULL, -- Steps (test step description)
    test_data TEXT, -- Test Data
    expected_result TEXT, -- Expected Result
    actual_result TEXT, -- Actual Result
    is_home BOOLEAN DEFAULT FALSE, -- Home column (green cell)
    url TEXT, -- URL info (if any)
    screenshot_path TEXT, -- Screenshot path
    status VARCHAR(50), -- PASS, FAIL, NOT_RUN
    start_time TIMESTAMP, -- Start time of test step
    end_time TIMESTAMP, -- End time of test step
    duration_ms BIGINT, -- Duration of test step (milliseconds)
    last_run TIMESTAMP, -- Last run time
    row_index INT, -- Row position in Excel
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (test_case_id) REFERENCES test_cases(id) ON DELETE CASCADE
);

-- Indexes for test steps
CREATE INDEX idx_teststep_test_case_id ON test_steps (test_case_id);
CREATE INDEX idx_teststep_step_number ON test_steps (step_number);

-- Test Case Excel Mapping table - To store positions of test cases in Excel
CREATE TABLE test_case_excel_mapping (
    id BIGSERIAL PRIMARY KEY,
    test_case_id BIGINT NOT NULL,
    excel_sheet_id BIGINT NOT NULL,
    start_row_index INT NOT NULL,
    end_row_index INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (test_case_id) REFERENCES test_cases(id) ON DELETE CASCADE,
    FOREIGN KEY (excel_sheet_id) REFERENCES excel_sheets(id) ON DELETE CASCADE
);

-- Indexes for Excel mapping
CREATE INDEX idx_excel_mapping_test_case_id ON test_case_excel_mapping (test_case_id);
CREATE INDEX idx_excel_mapping_sheet_id ON test_case_excel_mapping (excel_sheet_id);

-- Backlog Test Case Relation table - To relate backlog items with test cases
CREATE TABLE backlog_test_case_relation (
    id BIGSERIAL PRIMARY KEY,
    backlog_item_id BIGINT NOT NULL,
    test_case_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (backlog_item_id) REFERENCES product_backlog_items(id) ON DELETE CASCADE,
    FOREIGN KEY (test_case_id) REFERENCES test_cases(id) ON DELETE CASCADE,
    UNIQUE(backlog_item_id, test_case_id)
);
