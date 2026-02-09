-- 05_test_schedules.sql
-- This file contains tables related to test schedules

-- Dropping previous tables (if any)
DROP TABLE IF EXISTS test_schedules CASCADE;

-- Test Schedules table - To store test schedule information
CREATE TABLE test_schedules (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL,
    title VARCHAR(255), -- Optional title
    user_story_id VARCHAR(50) NOT NULL, -- e.g. US_01, US_02
    test_case_ids TEXT[] NOT NULL, -- Test case IDs as array ["TC01", "TC02"]
    start_time TIMESTAMP NOT NULL, -- Schedule start time
    end_time TIMESTAMP NOT NULL, -- Schedule end time
    schedule_type VARCHAR(20) NOT NULL DEFAULT 'once', -- once, daily, weekly, monthly
    status VARCHAR(20) NOT NULL DEFAULT 'scheduled', -- scheduled, running, completed, failed, paused, cancelled
    created_by VARCHAR(100), -- Creator user
    description TEXT, -- Optional description
    
    -- Scheduling information
    next_run_time TIMESTAMP, -- Next run time
    last_run_time TIMESTAMP, -- Last run time
    last_test_run_id BIGINT, -- Last executed test_run ID
    
    -- Repeat settings (in JSON format - for daily/weekly/monthly)
    repeat_settings JSONB, -- e.g. {"hour": 9, "minute": 0, "dayOfWeek": 1}
    
    -- Audit fields
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign key constraints
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    FOREIGN KEY (last_test_run_id) REFERENCES test_runs(id) ON DELETE SET NULL
);

-- Indexes for test schedules
CREATE INDEX idx_test_schedules_project_id ON test_schedules (project_id);
CREATE INDEX idx_test_schedules_user_story_id ON test_schedules (user_story_id);
CREATE INDEX idx_test_schedules_status ON test_schedules (status);
CREATE INDEX idx_test_schedules_next_run_time ON test_schedules (next_run_time);
CREATE INDEX idx_test_schedules_schedule_type ON test_schedules (schedule_type);
CREATE INDEX idx_test_schedules_created_at ON test_schedules (created_at);

-- Schedule Execution History table - To track schedule execution history
CREATE TABLE schedule_execution_history (
    id BIGSERIAL PRIMARY KEY,
    schedule_id BIGINT NOT NULL,
    test_run_id BIGINT NOT NULL,
    execution_time TIMESTAMP NOT NULL,
    execution_status VARCHAR(20) NOT NULL, -- success, failed, cancelled
    duration_minutes INTEGER,
    error_message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (schedule_id) REFERENCES test_schedules(id) ON DELETE CASCADE,
    FOREIGN KEY (test_run_id) REFERENCES test_runs(id) ON DELETE CASCADE
);

-- Indexes for schedule execution history
CREATE INDEX idx_schedule_execution_schedule_id ON schedule_execution_history (schedule_id);
CREATE INDEX idx_schedule_execution_test_run_id ON schedule_execution_history (test_run_id);
CREATE INDEX idx_schedule_execution_time ON schedule_execution_history (execution_time);

-- Comments for documentation
COMMENT ON TABLE test_schedules IS 'Test execution schedules - stores when and how tests should be automatically run';
COMMENT ON COLUMN test_schedules.schedule_type IS 'once: run once, daily: every day, weekly: every week, monthly: every month';
COMMENT ON COLUMN test_schedules.status IS 'scheduled: waiting to run, running: currently executing, completed: finished, failed: error occurred, paused: temporarily stopped, cancelled: permanently stopped';
COMMENT ON COLUMN test_schedules.repeat_settings IS 'JSON settings for recurring schedules: {"hour": 9, "minute": 0, "dayOfWeek": 1, "dayOfMonth": 15}';

COMMENT ON TABLE schedule_execution_history IS 'History of schedule executions - tracks when each schedule was run and results'; 