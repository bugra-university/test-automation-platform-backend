-- 03_test_runs_and_results.sql
-- Bu dosya, test çalıştırmaları ve sonuçlarıyla ilgili tabloları içerir

-- Önceki tabloların DROP edilmesi (eğer varsa)
DROP TABLE IF EXISTS screenshots CASCADE;
DROP TABLE IF EXISTS test_results CASCADE;
DROP TABLE IF EXISTS test_runs CASCADE;

-- Test Runs tablosu - Test koşularını saklamak için
CREATE TABLE test_runs (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL,
    name VARCHAR(255),
    start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    end_time TIMESTAMP,
    status VARCHAR(50), -- RUNNING, COMPLETED, FAILED, CANCELLED
    triggered_by VARCHAR(100), -- scheduled, manual, git_push
    environment VARCHAR(50), -- dev, test, prod
    parameters JSONB, -- Test koşusu parametreleri JSON formatında
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
);

-- Test koşuları için indeksler
CREATE INDEX idx_testrun_project_id ON test_runs (project_id);
CREATE INDEX idx_testrun_status ON test_runs (status);
CREATE INDEX idx_testrun_start_time ON test_runs (start_time);

-- Test Results tablosu - Test sonuçlarını saklamak için
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

-- Test sonuçları için indeksler
CREATE INDEX idx_testresult_test_run_id ON test_results (test_run_id);
CREATE INDEX idx_testresult_test_case_id ON test_results (test_case_id);
CREATE INDEX idx_testresult_status ON test_results (status);

-- Screenshots tablosu - Ekran görüntülerini saklamak için
CREATE TABLE screenshots (
    id BIGSERIAL PRIMARY KEY,
    test_result_id BIGINT NOT NULL,
    file_path VARCHAR(255) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    description TEXT,
    step_number INT, -- İlgili test adımı numarası (opsiyonel)
    FOREIGN KEY (test_result_id) REFERENCES test_results(id) ON DELETE CASCADE
);

-- Ekran görüntüleri için indeksler  
CREATE INDEX idx_screenshot_test_result_id ON screenshots (test_result_id);
