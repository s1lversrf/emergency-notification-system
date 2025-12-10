package com.example.emergencynotificationsystem.service;

import com.example.emergencynotificationsystem.dto.Recipient;
import com.example.emergencynotificationsystem.dto.UploadJob;
import com.example.emergencynotificationsystem.dto.UploadJobStatus;
import com.example.emergencynotificationsystem.repository.RecipientRepository;
import com.example.emergencynotificationsystem.repository.UploadJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileParsingService {

    private final RecipientRepository recipientRepository;
    private final UploadJobRepository uploadJobRepository;

    private final S3Service s3Service;
    private final BatchWriteService batchWriteService;

    @Async
    public void parseExcelFromS3Async(UploadJob uploadJob){
        int updateCount = uploadJobRepository.tryStartProcessing(uploadJob.getId(), LocalDateTime.now());
        if(updateCount == 0){
            log.info("Job {}: Already processing. Skipping", uploadJob.getId());
            return;
        }

        int startRow = uploadJob.getLastProcessedRow() != 0 ? uploadJob.getLastProcessedRow() + 1 : 1;

        log.info("Job {}: Starting from row {}", uploadJob.getId(), startRow);

        uploadJob.setStatus(UploadJobStatus.PROCESSING);
        uploadJobRepository.save(uploadJob);

        try (InputStream file = s3Service.downloadFile(uploadJob.getS3Key())){
            Workbook workbook = WorkbookFactory.create(file);
            Sheet sheet = workbook.getSheetAt(0);
            int totalRows = sheet.getLastRowNum();
            int processedRows = uploadJob.getProcessedRows();

            List<Recipient> batch = new ArrayList<>();

            for (int i = startRow; i <= totalRows; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Recipient recipient = new Recipient();

                recipient.setName(getCellValue(row.getCell(0)));
                recipient.setEmail(getCellValue(row.getCell(1)));

                batch.add(recipient);

                if (batch.size() >= 1000) {
                    batchWriteService.saveBatch(batch);

                    processedRows += batch.size();

                    uploadJob.setProcessedRows(processedRows);
                    uploadJob.setLastProcessedRow(i);
                    uploadJobRepository.save(uploadJob);

                    log.info("Job {}: Checkpoint at row {}. Processed: {}/{}",
                            uploadJob.getId(), i, processedRows, totalRows);

                    batch.clear();
                }
            }

            if (!batch.isEmpty()) {
                recipientRepository.saveAll(batch);

                processedRows += batch.size();

                uploadJob.setStatus(UploadJobStatus.COMPLETED);
                uploadJob.setProcessedRows(processedRows);
                uploadJob.setFinishedAt(LocalDateTime.now());
                uploadJobRepository.save(uploadJob);

                log.info("Job {}: Completed. Processed: {}/{}",
                        uploadJob.getId(), processedRows, totalRows);
            }
        } catch (Exception e) {
            log.error("Job {}: Failed at row {}", uploadJob.getId(), uploadJob.getLastProcessedRow(), e);

            uploadJob.setStatus(UploadJobStatus.FAILED);
            uploadJob.setErrorMessage(e.getMessage());
            uploadJob.setFinishedAt(LocalDateTime.now());
            uploadJobRepository.save(uploadJob);
        }
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