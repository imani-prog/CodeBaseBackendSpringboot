package com.example.codebasebackend.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDate;

@Embeddable
public class HealthRecordAttachment {

    @Column(length = 200)
    private String fileName;

    @Column(length = 300)
    private String fileUrl;

    @Column(length = 120)
    private String mimeType;

    @Column
    private Long sizeBytes;

    @Column(length = 60)
    private String category; // e.g., report, prescription, vaccination, insurance

    @Column(length = 120)
    private String uploadedBy;

    @Column
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

