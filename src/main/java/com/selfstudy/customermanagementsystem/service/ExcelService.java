package com.selfstudy.customermanagementsystem.service;

import com.selfstudy.customermanagementsystem.entity.Customer;
import com.selfstudy.customermanagementsystem.repository.CustomerRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelService {

    private final CustomerRepo repo;

    private static final int BATCH_SIZE = 1000;

    @Transactional
    public void processExcel(MultipartFile file) throws IOException {

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);

            List<Customer> batch = new ArrayList<>();

            for (Row row : sheet) {

                if (row.getRowNum() == 0) continue;

                Customer customer = new Customer();

                customer.setName(getStringValue(row, 0));
                customer.setDob(getDateValue(row, 1));
                customer.setNic(getStringValue(row, 2));


                repo.findByNic(customer.getNic()).ifPresentOrElse(existingCustomer -> {

                    existingCustomer.setName(customer.getName());
                    existingCustomer.setDob(customer.getDob());

                    batch.add(existingCustomer);

                }, () -> {

                    batch.add(customer);
                });


                if (batch.size() == BATCH_SIZE) {
                    repo.saveAll(batch);
                    batch.clear();
                }
            }

            if (!batch.isEmpty()) {
                repo.saveAll(batch);
            }
        }
    }

    private String getStringValue(Row row, int index) {
        Cell cell = row.getCell(index, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        return new DataFormatter().formatCellValue(cell).trim();
    }

    private LocalDate getDateValue(Row row, int index) {
        Cell cell = row.getCell(index, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                return cell.getLocalDateTimeCellValue().toLocalDate();
            }
        } catch (Exception e) {

            System.err.println("Invalid date value in row " + row.getRowNum() + " at column " + index + ": " + e.getMessage());
        }

        return null;
    }
}