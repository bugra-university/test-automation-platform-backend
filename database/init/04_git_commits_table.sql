-- Table storing Git commit information
CREATE TABLE IF NOT EXISTS git_commits (
    id BIGSERIAL PRIMARY KEY,
    commit_hash VARCHAR(100) NOT NULL UNIQUE,
    author VARCHAR(255),
    commit_date TIMESTAMP,
    message TEXT,
    branch VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
