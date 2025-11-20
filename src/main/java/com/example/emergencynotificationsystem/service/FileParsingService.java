package com.example.emergencynotificationsystem.service;

import com.example.emergencynotificationsystem.dto.Recipient;
import com.example.emergencynotificationsystem.repository.RecipientRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileParsingService {

    private final RecipientRepository recipientRepository;
    private final S3Service s3Service;

    @Async
    @SneakyThrows
    public void parseExcelFromS3Async(String s3Key){
        InputStream file = s3Service.downloadFile(s3Key);

        Workbook workbook = WorkbookFactory.create(file); //Sneaky throws
        Sheet sheet = workbook.getSheetAt(0);

        List<Recipient> batch = new ArrayList<>();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            Recipient recipient = new Recipient();

            recipient.setName(getCellValue(row.getCell(0)));
            recipient.setEmail(getCellValue(row.getCell(1)));

            batch.add(recipient);

            if (batch.size() >= 1000) {
                recipientRepository.saveAll(batch);
                log.info("Saved batch of {}", batch.size());
                batch.clear();
            }
        }

        if (!batch.isEmpty()) {
            recipientRepository.saveAll(batch);
        }

        workbook.close();
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((long) cell.getNumericCellValue());
        }
        return "";
    }
}