package com.example.emergencynotificationsystem.repository;

import com.example.emergencynotificationsystem.dto.Recipient;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipientRepository extends CrudRepository<Recipient, String> {}
