-- File tracking tables for smart sync functionality (PostgreSQL version)

-- Table to track file metadata and sync status
CREATE TABLE IF NOT EXISTS file_tracking_records (
    id VARCHAR(36) PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    file_hash VARCHAR(64) NOT NULL,
    content_hash VARCHAR(64) NOT NULL,
    last_sync_date TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for file_tracking_records
CREATE INDEX IF NOT EXISTS idx_file_name ON file_tracking_records (file_name);
CREATE INDEX IF NOT EXISTS idx_file_hash ON file_tracking_records (file_hash);
CREATE INDEX IF NOT EXISTS idx_content_hash ON file_tracking_records (content_hash);
CREATE INDEX IF NOT EXISTS idx_last_sync ON file_tracking_records (last_sync_date);

-- Table to store file content snapshots for comparison
CREATE TABLE IF NOT EXISTS file_content_storage (
    id VARCHAR(36) PRIMARY KEY,
    file_record_id VARCHAR(36) NOT NULL,
    content_json TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (file_record_id) REFERENCES file_tracking_records(id) ON DELETE CASCADE
);

-- Create indexes for file_content_storage
CREATE INDEX IF NOT EXISTS idx_file_record ON file_content_storage (file_record_id);
CREATE INDEX IF NOT EXISTS idx_created_at ON file_content_storage (created_at);

-- Insert initial record for file tracking (PostgreSQL version)
INSERT INTO file_tracking_records (id, file_name, file_hash, content_hash, last_sync_date, created_at, updated_at) 
VALUES 
(gen_random_uuid()::text, 'SYSTEM_INIT', 'SYSTEM', 'SYSTEM', NOW(), NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
