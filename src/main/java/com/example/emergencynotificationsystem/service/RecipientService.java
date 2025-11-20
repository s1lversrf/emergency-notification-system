package com.example.emergencynotificationsystem.service;

import com.example.emergencynotificationsystem.dto.Recipient;
import com.example.emergencynotificationsystem.dto.RecipientCreateRequest;
import com.example.emergencynotificationsystem.repository.RecipientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecipientService {
    private final RecipientRepository recipientRepository;

    public String addRecipient(RecipientCreateRequest request) {
        Recipient recipient = new Recipient(request.name(), request.email());

        recipientRepository.save(recipient);
        return recipient.getId();
    }

    public Recipient getRecipientById(String id) {
        return recipientRepository.findById(id).orElse(null);
    }

    public void deleteAllRecipients() {
        recipientRepository.deleteAll();
    }
}
