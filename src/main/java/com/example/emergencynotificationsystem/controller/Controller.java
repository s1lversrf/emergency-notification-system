package com.example.emergencynotificationsystem.controller;

import com.example.emergencynotificationsystem.dto.Recipient;
import com.example.emergencynotificationsystem.dto.RecipientCreateRequest;
import com.example.emergencynotificationsystem.dto.RecipientResponse;
import com.example.emergencynotificationsystem.service.RecipientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/emergency-notification-system")
public class Controller {
    private final RecipientService recipientService;

    @PostMapping
    public ResponseEntity<String> addRecipient(@RequestBody RecipientCreateRequest request) {
        String id = recipientService.addRecipient(request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(id);
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
}
