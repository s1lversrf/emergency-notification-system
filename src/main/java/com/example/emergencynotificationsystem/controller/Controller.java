package com.example.emergencynotificationsystem.controller;

import com.example.emergencynotificationsystem.dto.Recipient;
import com.example.emergencynotificationsystem.dto.RecipientCreateRequest;
import com.example.emergencynotificationsystem.dto.RecipientResponse;
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
    private final S3Service s3Service;
    private final FileParsingService fileParsingService;

    @PostMapping
    public ResponseEntity<String> addRecipient(@RequestBody RecipientCreateRequest request) {
        String id = recipientService.addRecipient(request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(id);
    }

    @PostMapping("/upload")
    public ResponseEntity<Void> uploadFile (@RequestParam MultipartFile file) {
        String s3Key = s3Service.uploadFile(file);

        fileParsingService.parseExcelFromS3Async(s3Key);

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
