-- Default project creation
INSERT INTO projects (name, description)
VALUES ('Project Team09', 'Test Automation Project');

-- Default test suite creation  
INSERT INTO test_suites (project_id, name, description)
VALUES ((SELECT id FROM projects WHERE name='Project Team09'), 'Test Automation Suite', 'Main Test Suite for the project');
