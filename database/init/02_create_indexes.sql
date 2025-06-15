-- "Indexes for test cases - For fast queries
CREATE INDEX IF NOT EXISTS idx_test_cases_class_name ON test_cases(class_name);
CREATE INDEX IF NOT EXISTS idx_test_cases_method_name ON test_cases(method_name);
CREATE INDEX IF NOT EXISTS idx_test_cases_suite_id ON test_cases(suite_id);

-- "Indexes for test runs - Performance optimization
CREATE INDEX IF NOT EXISTS idx_test_runs_status ON test_runs(status);
CREATE INDEX IF NOT EXISTS idx_test_runs_start_time ON test_runs(start_time);
CREATE INDEX IF NOT EXISTS idx_test_runs_git_commit_hash ON test_runs(git_commit_hash);

-- Indexes for test results - For relational queries
CREATE INDEX IF NOT EXISTS idx_test_results_status ON test_results(status);
CREATE INDEX IF NOT EXISTS idx_test_results_test_run_id ON test_results(test_run_id);
CREATE INDEX IF NOT EXISTS idx_test_results_test_case_id ON test_results(test_case_id);
CREATE INDEX IF NOT EXISTS idx_test_results_start_time ON test_results(start_time);

-- Indexes for screenshots
CREATE INDEX IF NOT EXISTS idx_screenshots_test_result_id ON screenshots(test_result_id);
CREATE INDEX IF NOT EXISTS idx_screenshots_step_id ON screenshots(step_id);

-- Indexes for reports
CREATE INDEX IF NOT EXISTS idx_reports_test_run_id ON reports(test_run_id);
CREATE INDEX IF NOT EXISTS idx_reports_report_type ON reports(report_type);

-- Indexes for scheduled jobs
CREATE INDEX IF NOT EXISTS idx_scheduled_jobs_is_active ON scheduled_jobs(is_active);
CREATE INDEX IF NOT EXISTS idx_scheduled_jobs_next_run ON scheduled_jobs(next_run);

-- Indexes for users
CREATE INDEX IF NOT EXISTS idx_users_keycloak_id ON users(keycloak_id);

-- Indexes for git commits
-- Commented out because git_commits table doesn't exist
-- CREATE INDEX idx_git_commits_hash ON git_commits(commit_hash);
