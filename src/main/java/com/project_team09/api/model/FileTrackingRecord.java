package com.project_team09.api.model;

import java.time.LocalDateTime;

public class FileTrackingRecord {
    private String id;
    private String fileName;
    private String fileHash;
    private String contentHash;
    private LocalDateTime lastSyncDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public FileTrackingRecord() {
    }

    public FileTrackingRecord(String fileName, String fileHash, String contentHash) {
        this.fileName = fileName;
        this.fileHash = fileHash;
        this.contentHash = contentHash;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }

    public String getContentHash() {
        return contentHash;
    }

    public void setContentHash(String contentHash) {
        this.contentHash = contentHash;
    }

    public LocalDateTime getLastSyncDate() {
        return lastSyncDate;
    }

    public void setLastSyncDate(LocalDateTime lastSyncDate) {
        this.lastSyncDate = lastSyncDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "FileTrackingRecord{" +
                "id='" + id + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileHash='" + fileHash + '\'' +
                ", contentHash='" + contentHash + '\'' +
                ", lastSyncDate=" + lastSyncDate +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
