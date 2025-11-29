package com.example.emergencynotificationsystem.service;

import com.example.emergencynotificationsystem.dto.Recipient;
import com.example.emergencynotificationsystem.repository.RecipientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BatchWriteService {
    private final RecipientRepository recipientRepository;

    @Transactional
    protected void saveBatch(List<Recipient> batch) {
        recipientRepository.saveAll(batch);
    }
}
