-- 01_create_schema.sql
-- Bu dosya, projeler ve Excel dosyalarıyla ilgili tabloları içerir

-- Önceki tabloların DROP edilmesi (eğer varsa)
DROP TABLE IF EXISTS excel_sheets CASCADE;
DROP TABLE IF EXISTS project_excel_files CASCADE;
DROP TABLE IF EXISTS projects CASCADE;

-- Projects tablosu - Projelerin ana tablosu
CREATE TABLE projects (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    owner_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Projeler için indeks oluşturma
CREATE INDEX idx_project_name ON projects (name);
CREATE INDEX idx_project_owner_id ON projects (owner_id);

-- Proje Excel Dosyaları tablosu - Excel dosyalarını projelerle ilişkilendirmek için
CREATE TABLE project_excel_files (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(255) NOT NULL,
    original_file_name VARCHAR(255),
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    file_hash VARCHAR(64),
    content_hash VARCHAR(64),
    file_size BIGINT,
    sheet_count INT DEFAULT 0,
    processing_status VARCHAR(50) DEFAULT 'PENDING', -- PENDING, PROCESSING, COMPLETED, ERROR
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
);

-- Excel dosyaları için indeksler
CREATE INDEX idx_excel_file_name ON project_excel_files (file_name);
CREATE INDEX idx_excel_project_id ON project_excel_files (project_id);
CREATE INDEX idx_excel_upload_date ON project_excel_files (upload_date);

-- Excel Sayfaları tablosu - Excel dosyalarındaki sayfaları takip etmek için
CREATE TABLE excel_sheets (
    id BIGSERIAL PRIMARY KEY,
    excel_file_id BIGINT NOT NULL,
    sheet_name VARCHAR(255) NOT NULL,
    sheet_type VARCHAR(50) NOT NULL, -- BACKLOG, TEST_CASE, OTHER
    sheet_index INT,
    row_count INT DEFAULT 0,
    column_count INT DEFAULT 0,
    parsed BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (excel_file_id) REFERENCES project_excel_files(id) ON DELETE CASCADE
);

-- Excel sayfaları için indeksler
CREATE INDEX idx_sheet_excel_file_id ON excel_sheets (excel_file_id);
CREATE INDEX idx_sheet_name ON excel_sheets (sheet_name);
CREATE INDEX idx_sheet_type ON excel_sheets (sheet_type);

-- Varsayılan proje oluşturma (owner_id = 1 varsayılan admin kullanıcısı)
INSERT INTO projects (name, description, owner_id, created_at, updated_at)
VALUES ('Default Project', 'Otomatik olarak oluşturulan varsayılan proje', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
