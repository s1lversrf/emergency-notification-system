package com.example.emergencynotificationsystem.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @SneakyThrows
    public String uploadFile(MultipartFile file) {
        String s3key = UUID.randomUUID().toString();

        InputStream inputStream = new ByteArrayInputStream(file.getBytes()); //Sneaky throws

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        amazonS3.putObject(bucketName, s3key, inputStream, objectMetadata);

        return s3key;
    }

    public InputStream downloadFile(String s3Key) {
        return amazonS3.getObject(bucketName, s3Key).getObjectContent();
    }
}