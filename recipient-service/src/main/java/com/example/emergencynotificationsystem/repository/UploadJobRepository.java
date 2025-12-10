package com.example.emergencynotificationsystem.repository;

import com.example.emergencynotificationsystem.dto.UploadJob;
import com.example.emergencynotificationsystem.dto.UploadJobStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UploadJobRepository extends CrudRepository<UploadJob, String> {
    @Query("SELECT j FROM UploadJob j WHERE j.status = :status")
    List<UploadJob> findByStatus(UploadJobStatus status);

    @Modifying
    @Transactional
    @Query("""
        UPDATE UploadJob u SET u.status = com.example.emergencynotificationsystem.dto.UploadJobStatus.PROCESSING, u.startedAt = :now
        WHERE u.id = :id AND u.status IN (com.example.emergencynotificationsystem.dto.UploadJobStatus.UPLOADED, com.example.emergencynotificationsystem.dto.UploadJobStatus.FAILED)
        """)
    int tryStartProcessing(@Param("id") String id, @Param("now") LocalDateTime now);

}