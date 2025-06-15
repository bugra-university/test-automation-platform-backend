-- Excel entegrasyonu için test yönetimi tablolarına ek alanlar

-- Test cases tablosuna ek alanlar
ALTER TABLE test_cases 
ADD COLUMN IF NOT EXISTS test_case_id VARCHAR(10), -- TC01, TC02 gibi
ADD COLUMN IF NOT EXISTS user_story_id VARCHAR(10), -- US01, US02 gibi
ADD COLUMN IF NOT EXISTS test_objective TEXT,
ADD COLUMN IF NOT EXISTS pre_condition TEXT;

-- Test steps tablosuna ek alanlar
ALTER TABLE test_steps 
ADD COLUMN IF NOT EXISTS test_case_id BIGINT,
ADD COLUMN IF NOT EXISTS step_number INTEGER,
ADD COLUMN IF NOT EXISTS step_description TEXT,
ADD COLUMN IF NOT EXISTS test_data TEXT,
ADD COLUMN IF NOT EXISTS expected_result TEXT,
ADD COLUMN IF NOT EXISTS is_home BOOLEAN DEFAULT FALSE;

-- Test results tablosuna ek alan
ALTER TABLE test_results 
ADD COLUMN IF NOT EXISTS actual_result TEXT;

-- İndeksler
CREATE INDEX IF NOT EXISTS idx_test_cases_test_case_id ON test_cases(test_case_id);
CREATE INDEX IF NOT EXISTS idx_test_cases_user_story_id ON test_cases(user_story_id);

-- Test case ve test steps tabloları arasındaki ilişki için foreign key
DO $$ 
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_test_steps_test_case'
    ) THEN
        ALTER TABLE test_steps
        ADD CONSTRAINT fk_test_steps_test_case
        FOREIGN KEY (test_case_id) REFERENCES test_cases(id)
        ON DELETE CASCADE;
    END IF;
END $$;
