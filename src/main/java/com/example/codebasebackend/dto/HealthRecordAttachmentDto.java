package com.example.codebasebackend.dto;

import java.time.LocalDate;

public class HealthRecordAttachmentDto {
    private String fileName;
    private String fileUrl;
    private String mimeType;
    private Long sizeBytes;
    private String category;
    private String uploadedBy;
    private LocalDate uploadDate;

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    public Long getSizeBytes() { return sizeBytes; }
    public void setSizeBytes(Long sizeBytes) { this.sizeBytes = sizeBytes; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(String uploadedBy) { this.uploadedBy = uploadedBy; }
    public LocalDate getUploadDate() { return uploadDate; }
    public void setUploadDate(LocalDate uploadDate) { this.uploadDate = uploadDate; }
}

