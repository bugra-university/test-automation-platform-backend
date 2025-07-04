-- 02_backlog_and_testcases.sql
-- Bu dosya, product backlog ve test case'lerle ilgili tabloları içerir

-- Önceki tabloların DROP edilmesi (eğer varsa)
DROP TABLE IF EXISTS backlog_test_case_relation CASCADE;
DROP TABLE IF EXISTS test_steps CASCADE;
DROP TABLE IF EXISTS test_cases CASCADE;
DROP TABLE IF EXISTS product_backlog_items CASCADE;
DROP TABLE IF EXISTS test_case_excel_mapping CASCADE;

-- Product Backlog Items tablosu - Ürün backlog öğelerini saklamak için
CREATE TABLE product_backlog_items (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL,
    excel_sheet_id BIGINT NOT NULL,
    user_story_id VARCHAR(50) NOT NULL, -- US_01, US_02 gibi
    description TEXT,
    team VARCHAR(255),
    acceptance_criteria TEXT,
    home_quality VARCHAR(100), -- High Quality gibi
    validation_type VARCHAR(50), -- UI gibi
    row_index INT, -- Excel'deki satır konumu
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    FOREIGN KEY (excel_sheet_id) REFERENCES excel_sheets(id) ON DELETE CASCADE
);

-- Backlog öğeleri için indeksler
CREATE INDEX idx_backlog_project_id ON product_backlog_items (project_id);
CREATE INDEX idx_backlog_sheet_id ON product_backlog_items (excel_sheet_id);
CREATE INDEX idx_backlog_user_story_id ON product_backlog_items (user_story_id);

-- Test Cases tablosu - Test senaryolarını saklamak için
CREATE TABLE test_cases (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL,
    excel_sheet_id BIGINT NOT NULL,
    user_story_id VARCHAR(50) NOT NULL, -- US01 gibi
    test_case_id VARCHAR(50) NOT NULL, -- TC01 gibi
    test_objective TEXT NOT NULL, -- "Sign up when all areas are filled" gibi
    pre_condition TEXT, -- "Access to the Site" gibi
    notes TEXT, -- Notlar veya BUG açıklamaları
    status VARCHAR(50) DEFAULT 'ACTIVE', -- ACTIVE, DEPRECATED
    scenario_type VARCHAR(20), -- Positive/Negative scenario
    bug_notes TEXT[], -- BUG notları array olarak
    row_index INT, -- Excel'deki başlangıç satırı
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    FOREIGN KEY (excel_sheet_id) REFERENCES excel_sheets(id) ON DELETE CASCADE,
    UNIQUE(project_id, user_story_id, test_case_id) -- Her projede benzersiz user_story + test_case ID kombinasyonu
);

-- Test case'ler için indeksler
CREATE INDEX idx_testcase_project_id ON test_cases (project_id);
CREATE INDEX idx_testcase_sheet_id ON test_cases (excel_sheet_id);
CREATE INDEX idx_testcase_user_story_id ON test_cases (user_story_id);
CREATE INDEX idx_testcase_test_case_id ON test_cases (test_case_id);

-- Test Steps tablosu - Test adımlarını saklamak için
CREATE TABLE test_steps (
    id BIGSERIAL PRIMARY KEY,
    test_case_id BIGINT NOT NULL,
    step_number INT NOT NULL, -- No (sıra numarası)
    step_description TEXT NOT NULL, -- Steps (test adımı açıklaması)
    test_data TEXT, -- Test Data (test verisi)
    expected_result TEXT, -- Expected Result (beklenen sonuç)
    actual_result TEXT, -- Actual Result (gerçek sonuç)
    is_home BOOLEAN DEFAULT FALSE, -- Home kolonu (yeşil hücre)
    url TEXT, -- URL bilgisi (varsa)
    screenshot_path TEXT, -- Ekran görüntüsü yolu
    status VARCHAR(50), -- PASS, FAIL, NOT_RUN
    start_time TIMESTAMP, -- Test adımının başlangıç zamanı
    end_time TIMESTAMP, -- Test adımının bitiş zamanı
    duration_ms BIGINT, -- Test adımının süresi (milisaniye)
    last_run TIMESTAMP, -- Son çalıştırılma zamanı
    row_index INT, -- Excel'deki satır konumu
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (test_case_id) REFERENCES test_cases(id) ON DELETE CASCADE
);

-- Test adımları için indeksler
CREATE INDEX idx_teststep_test_case_id ON test_steps (test_case_id);
CREATE INDEX idx_teststep_step_number ON test_steps (step_number);

-- Test Case Excel Mapping tablosu - Test case'lerin Excel içindeki konumlarını saklamak için
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

-- Excel mapping için indeksler
CREATE INDEX idx_excel_mapping_test_case_id ON test_case_excel_mapping (test_case_id);
CREATE INDEX idx_excel_mapping_sheet_id ON test_case_excel_mapping (excel_sheet_id);

-- Backlog Test Case Relation tablosu - Backlog öğeleri ile test case'leri ilişkilendirmek için
CREATE TABLE backlog_test_case_relation (
    id BIGSERIAL PRIMARY KEY,
    backlog_item_id BIGINT NOT NULL,
    test_case_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (backlog_item_id) REFERENCES product_backlog_items(id) ON DELETE CASCADE,
    FOREIGN KEY (test_case_id) REFERENCES test_cases(id) ON DELETE CASCADE,
    UNIQUE(backlog_item_id, test_case_id)
);
