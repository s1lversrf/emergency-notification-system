package com.example.emergencynotificationsystem.service;

import com.example.emergencynotificationsystem.dto.UploadJob;
import com.example.emergencynotificationsystem.dto.UploadJobStatus;
import com.example.emergencynotificationsystem.repository.UploadJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UploadRetryService {
    private final UploadJobRepository uploadJobRepository;
    private final FileParsingService fileParsingService;

    @Scheduled(fixedRate = 300000)
    public void uploadRetry() {
        List<UploadJob> failedUploadJobs = uploadJobRepository.findByStatus(UploadJobStatus.FAILED);

        for (UploadJob uploadJob : failedUploadJobs) {
            if (uploadJob.getRetryCount() < 3) {
                log.info("Job {}: Retrying, attempt {}", uploadJob.getId(), uploadJob.getRetryCount());

                uploadJob.setRetryCount(uploadJob.getRetryCount() + 1);
                uploadJobRepository.save(uploadJob);

                fileParsingService.parseExcelFromS3Async(uploadJob);
            } else {
                log.warn("Job {}: Exceeded max retry count 3", uploadJob.getId());
            }
        }
    }
}
