package com.project_team09.api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import com.project_team09.api.model.FileTrackingRecord;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FileTrackingService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String INSERT_FILE_RECORD = "INSERT INTO file_tracking_records (id, file_name, file_hash, content_hash, created_at, updated_at) "
            +
            "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_SYNC_STATUS = "UPDATE file_tracking_records SET last_sync_date = ?, updated_at = ? WHERE file_name = ?";

    private static final String GET_FILE_RECORD = "SELECT * FROM file_tracking_records WHERE file_name = ? ORDER BY created_at DESC LIMIT 1";

    private static final String INSERT_FILE_CONTENT = "INSERT INTO file_content_storage (id, file_record_id, content_json, created_at) VALUES (?, ?, ?, ?)";

    private static final String GET_FILE_CONTENT = "SELECT fcs.content_json FROM file_content_storage fcs " +
            "JOIN file_tracking_records ftr ON fcs.file_record_id = ftr.id " +
            "WHERE ftr.id = ? ORDER BY fcs.created_at DESC LIMIT 1";

    public FileTrackingRecord getFileRecord(String fileName) {
        try {
            List<FileTrackingRecord> records = jdbcTemplate.query(
                    GET_FILE_RECORD,
                    new FileTrackingRecordRowMapper(),
                    fileName);
            return records.isEmpty() ? null : records.get(0);
        } catch (Exception e) {
            System.err.println("Error getting file record: " + e.getMessage());
            return null;
        }
    }

    public void updateSyncStatus(String fileName, String fileHash, String contentHash, String syncDateStr) {
        try {
            // Parse ISO 8601 date format (from frontend's toISOString())
            LocalDateTime syncDate;
            if (syncDateStr.endsWith("Z")) {
                // ISO 8601 format with Z suffix (UTC)
                syncDate = LocalDateTime.parse(syncDateStr.substring(0, syncDateStr.length() - 1));
            } else {
                // ISO Local DateTime format
                syncDate = LocalDateTime.parse(syncDateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }

            LocalDateTime now = LocalDateTime.now();

            // First, check if record exists
            FileTrackingRecord existingRecord = getFileRecord(fileName);

            if (existingRecord == null) {
                // Create new record
                String recordId = UUID.randomUUID().toString();
                jdbcTemplate.update(
                        INSERT_FILE_RECORD,
                        recordId, fileName, fileHash, contentHash, now, now);
            }

            // Update sync status
            jdbcTemplate.update(UPDATE_SYNC_STATUS, syncDate, now, fileName);

            System.out.println("Updated sync status for file: " + fileName);
        } catch (Exception e) {
            System.err.println("Error updating sync status: " + e.getMessage());
            throw new RuntimeException("Failed to update sync status", e);
        }
    }

    public String storeFileContent(String fileName, String fileHash, String contentHash, Map<String, Object> content) {
        try {
            // Get or create file record
            FileTrackingRecord record = getFileRecord(fileName);
            String recordId;

            if (record == null) {
                recordId = UUID.randomUUID().toString();
                LocalDateTime now = LocalDateTime.now();
                jdbcTemplate.update(
                        INSERT_FILE_RECORD,
                        recordId, fileName, fileHash, contentHash, now, now);
            } else {
                recordId = record.getId();
            }

            // Store content
            String contentId = UUID.randomUUID().toString();
            String contentJson = objectMapper.writeValueAsString(content);

            jdbcTemplate.update(
                    INSERT_FILE_CONTENT,
                    contentId, recordId, contentJson, LocalDateTime.now());

            System.out.println("Stored content for file: " + fileName);
            return recordId;
        } catch (Exception e) {
            System.err.println("Error storing file content: " + e.getMessage());
            throw new RuntimeException("Failed to store file content", e);
        }
    }

    public Map<String, Object> getFileContent(String recordId) {
        try {
            List<String> contentJsonList = jdbcTemplate.queryForList(
                    GET_FILE_CONTENT,
                    String.class,
                    recordId);

            if (contentJsonList.isEmpty()) {
                return null;
            }

            String contentJson = contentJsonList.get(0);
            return objectMapper.readValue(contentJson, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            System.err.println("Error getting file content: " + e.getMessage());
            return null;
        }
    }

    /**
     * Get all file records from database
     */
    public List<Map<String, Object>> getAllFileRecords() {
        try {
            String query = "SELECT * FROM file_tracking_records ORDER BY created_at DESC";
            List<FileTrackingRecord> records = jdbcTemplate.query(query, new FileTrackingRecordRowMapper());

            // Convert to map format for frontend
            return records.stream().map(record -> {
                Map<String, Object> fileInfo = new HashMap<>();
                fileInfo.put("id", record.getId());
                fileInfo.put("fileName", record.getFileName());
                fileInfo.put("fileHash", record.getFileHash());
                fileInfo.put("contentHash", record.getContentHash());
                fileInfo.put("lastSyncDate", record.getLastSyncDate());
                fileInfo.put("createdAt", record.getCreatedAt());
                fileInfo.put("updatedAt", record.getUpdatedAt());
                return fileInfo;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error getting all file records: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Delete file record and associated content from database
     */
    public boolean deleteFileRecord(String fileName) {
        try {
            // First delete associated content
            String deleteContentQuery = "DELETE FROM file_content_storage WHERE file_record_id IN " +
                    "(SELECT id FROM file_tracking_records WHERE file_name = ?)";
            jdbcTemplate.update(deleteContentQuery, fileName);

            // Then delete the file record
            String deleteRecordQuery = "DELETE FROM file_tracking_records WHERE file_name = ?";
            int deletedRows = jdbcTemplate.update(deleteRecordQuery, fileName);

            System.out.println("Deleted file record for: " + fileName + " (rows affected: " + deletedRows + ")");
            return deletedRows > 0;
        } catch (Exception e) {
            System.err.println("Error deleting file record: " + e.getMessage());
            return false;
        }
    }

    private static class FileTrackingRecordRowMapper implements RowMapper<FileTrackingRecord> {
        @Override
        public FileTrackingRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
            FileTrackingRecord record = new FileTrackingRecord();
            record.setId(rs.getString("id"));
            record.setFileName(rs.getString("file_name"));
            record.setFileHash(rs.getString("file_hash"));
            record.setContentHash(rs.getString("content_hash"));

            if (rs.getTimestamp("last_sync_date") != null) {
                record.setLastSyncDate(rs.getTimestamp("last_sync_date").toLocalDateTime());
            }

            if (rs.getTimestamp("created_at") != null) {
                record.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            }

            if (rs.getTimestamp("updated_at") != null) {
                record.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            }

            return record;
        }
    }
}
