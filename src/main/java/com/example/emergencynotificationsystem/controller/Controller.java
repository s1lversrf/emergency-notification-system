package com.example.emergencynotificationsystem.controller;

import com.example.emergencynotificationsystem.dto.*;
import com.example.emergencynotificationsystem.repository.UploadJobRepository;
import com.example.emergencynotificationsystem.service.FileParsingService;
import com.example.emergencynotificationsystem.service.RecipientService;
import com.example.emergencynotificationsystem.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/emergency-notification-system")
public class Controller {
    private final RecipientService recipientService;
    private final FileParsingService fileParsingService;
    private final S3Service s3Service;

    private final UploadJobRepository uploadJobRepository;

    @PostMapping
    public ResponseEntity<String> addRecipient(@RequestBody RecipientCreateRequest request) {
        String id = recipientService.addRecipient(request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(id);
    }

    @PostMapping("/upload")
    public ResponseEntity<Void> uploadFile (@RequestParam("file") MultipartFile file) {
        UploadJob uploadJob = new UploadJob(UploadJobStatus.CREATED);
        uploadJobRepository.save(uploadJob);

        String s3Key = s3Service.uploadFile(file);

        uploadJob.setS3Key(s3Key);
        uploadJob.setStatus(UploadJobStatus.UPLOADED);
        uploadJobRepository.save(uploadJob);

        fileParsingService.parseExcelFromS3Async(uploadJob);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipientResponse> getRecipientById(@PathVariable("id") String id) {
        Recipient recipient = recipientService.getRecipientById(id);

        RecipientResponse response = new RecipientResponse(
                recipient.getId(),
                recipient.getName(),
                recipient.getEmail());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllRecipients() {
        recipientService.deleteAllRecipients();

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
