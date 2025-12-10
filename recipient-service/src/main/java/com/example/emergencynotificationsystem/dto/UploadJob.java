package com.example.emergencynotificationsystem.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class UploadJob {
    @Id
    private String id;
    private String s3Key;
    @Enumerated(EnumType.STRING)
    private UploadJobStatus status;
    private String errorMessage;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private int retryCount;
    private int processedRows;
    private int lastProcessedRow;

    public UploadJob(UploadJobStatus status) {
        this.status = status;
        this.id = UUID.randomUUID().toString();
    }
}
