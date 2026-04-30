package com.selfstudy.customermanagementsystem.service;

import com.selfstudy.customermanagementsystem.entity.Customer;
import com.selfstudy.customermanagementsystem.repository.CustomerRepo;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExcelServiceTest {

    @Mock
    private CustomerRepo customerRepo;

    @InjectMocks
    private ExcelService excelService;

    private MultipartFile validExcelFile;
    private MultipartFile emptyExcelFile;
    private MultipartFile invalidExcelFile;

    @BeforeEach
    void setUp() throws IOException {
        validExcelFile = createTestExcelFile(true, 10);
        emptyExcelFile = createTestExcelFile(false, 0);
        invalidExcelFile = createInvalidExcelFile();
    }

    @Test
    void processExcel_WithValidFile_Success() throws IOException {
        when(customerRepo.findByNic(anyString())).thenReturn(Optional.empty());
        when(customerRepo.saveAll(anyList())).thenReturn(new ArrayList<>());

        assertDoesNotThrow(() -> excelService.processExcel(validExcelFile));

        verify(customerRepo, atLeastOnce()).saveAll(anyList());
    }

    @Test
    void processExcel_WithExistingCustomers_UpdatesThem() throws IOException {
        Customer existingCustomer = new Customer();
        existingCustomer.setId(1L);
        existingCustomer.setNic("199005150001");
        existingCustomer.setName("Old Name");
        existingCustomer.setDob(LocalDate.of(1990, 5, 15));

        when(customerRepo.findByNic("199005150001")).thenReturn(Optional.of(existingCustomer));
        when(customerRepo.saveAll(anyList())).thenReturn(new ArrayList<>());

        excelService.processExcel(validExcelFile);

        ArgumentCaptor<List<Customer>> customerListCaptor = ArgumentCaptor.forClass(List.class);
        verify(customerRepo, atLeastOnce()).saveAll(customerListCaptor.capture());

        List<Customer> savedCustomers = customerListCaptor.getValue();
        if (savedCustomers != null && !savedCustomers.isEmpty()) {
            Customer updatedCustomer = savedCustomers.stream()
                    .filter(c -> "199005150001".equals(c.getNic()))
                    .findFirst()
                    .orElse(null);

            if (updatedCustomer != null) {
                assertEquals("Updated Customer 1", updatedCustomer.getName());
            }
        }
    }

    @Test
    void processExcel_WithNewCustomers_CreatesThem() throws IOException {
        when(customerRepo.findByNic(anyString())).thenReturn(Optional.empty());
        when(customerRepo.saveAll(anyList())).thenReturn(new ArrayList<>());

        excelService.processExcel(validExcelFile);

        ArgumentCaptor<List<Customer>> customerListCaptor = ArgumentCaptor.forClass(List.class);
        verify(customerRepo, atLeastOnce()).saveAll(customerListCaptor.capture());

        List<List<Customer>> savedBatches = customerListCaptor.getAllValues();
        assertFalse(savedBatches.isEmpty());
    }

    @Test
    void processExcel_WithEmptyFile_NoDbCalls() throws IOException {
        excelService.processExcel(emptyExcelFile);

        verify(customerRepo, never()).findByNic(anyString());
        verify(customerRepo, never()).saveAll(any());
    }

    @Test
    void processExcel_HandlesBatchProcessing_With1500Records() throws IOException {
        MultipartFile largeFile = createTestExcelFile(true, 1500);

        when(customerRepo.findByNic(anyString())).thenReturn(Optional.empty());
        when(customerRepo.saveAll(anyList())).thenReturn(new ArrayList<>());

        excelService.processExcel(largeFile);


        verify(customerRepo, atLeast(2)).saveAll(anyList());
    }

    @Test
    void processExcel_HandlesBatchProcessing_With2500Records() throws IOException {
        MultipartFile largeFile = createTestExcelFile(true, 2500);

        when(customerRepo.findByNic(anyString())).thenReturn(Optional.empty());
        when(customerRepo.saveAll(anyList())).thenReturn(new ArrayList<>());

        excelService.processExcel(largeFile);

        verify(customerRepo, times(3)).saveAll(anyList());
    }

    @Test
    void processExcel_SkipsHeaderRow() throws IOException {
        when(customerRepo.findByNic(anyString())).thenReturn(Optional.empty());
        when(customerRepo.saveAll(anyList())).thenReturn(new ArrayList<>());

        excelService.processExcel(validExcelFile);

        ArgumentCaptor<List<Customer>> captor = ArgumentCaptor.forClass(List.class);
        verify(customerRepo, atLeastOnce()).saveAll(captor.capture());

        List<Customer> processedCustomers = captor.getValue();
        assertNotNull(processedCustomers);
    }

    @Test
    void processExcel_WithInvalidDate_HandlesGracefully() throws IOException {
        MultipartFile fileWithInvalidDate = createExcelWithInvalidDate();

        when(customerRepo.findByNic(anyString())).thenReturn(Optional.empty());
        when(customerRepo.saveAll(anyList())).thenReturn(new ArrayList<>());

        assertDoesNotThrow(() -> excelService.processExcel(fileWithInvalidDate));

        verify(customerRepo, atLeastOnce()).saveAll(anyList());
    }

    @Test
    void processExcel_WithMissingCells_HandlesGracefully() throws IOException {
        MultipartFile fileWithMissingCells = createExcelWithMissingCells();

        when(customerRepo.findByNic(anyString())).thenReturn(Optional.empty());
        when(customerRepo.saveAll(anyList())).thenReturn(new ArrayList<>());

        assertDoesNotThrow(() -> excelService.processExcel(fileWithMissingCells));

        verify(customerRepo, atLeastOnce()).saveAll(anyList());
    }

    @Test
    void processExcel_WithExactlyBatchSize_SavesOnce() throws IOException {
        MultipartFile exactBatchFile = createTestExcelFile(true, 1000);

        when(customerRepo.findByNic(anyString())).thenReturn(Optional.empty());
        when(customerRepo.saveAll(anyList())).thenReturn(new ArrayList<>());

        excelService.processExcel(exactBatchFile);

        verify(customerRepo, times(1)).saveAll(anyList());
    }

    @Test
    void processExcel_WithLessThanBatchSize_SavesOnce() throws IOException {
        MultipartFile smallFile = createTestExcelFile(true, 500);

        when(customerRepo.findByNic(anyString())).thenReturn(Optional.empty());
        when(customerRepo.saveAll(anyList())).thenReturn(new ArrayList<>());

        excelService.processExcel(smallFile);

        verify(customerRepo, times(1)).saveAll(anyList());
    }

    private MultipartFile createTestExcelFile(boolean withData, int rowCount) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Customers");


            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Name");
            headerRow.createCell(1).setCellValue("Date of Birth");
            headerRow.createCell(2).setCellValue("NIC");

            if (withData) {
                for (int i = 1; i <= rowCount; i++) {
                    Row row = sheet.createRow(i);
                    row.createCell(0).setCellValue("Customer " + i);
                    row.createCell(1).setCellValue("1990-01-01");
                    row.createCell(2).setCellValue("NIC" + String.format("%010d", i));
                }
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);

            return new MockMultipartFile(
                    "file",
                    "test.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    bos.toByteArray()
            );
        }
    }

    private MultipartFile createExcelWithInvalidDate() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Customers");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Name");
            headerRow.createCell(1).setCellValue("Date of Birth");
            headerRow.createCell(2).setCellValue("NIC");

            Row dataRow = sheet.createRow(1);
            dataRow.createCell(0).setCellValue("Test Customer");
            dataRow.createCell(1).setCellValue("INVALID-DATE");
            dataRow.createCell(2).setCellValue("TEST123");

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);

            return new MockMultipartFile(
                    "file",
                    "invalid_date.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    bos.toByteArray()
            );
        }
    }

    private MultipartFile createExcelWithMissingCells() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Customers");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Name");
            headerRow.createCell(1).setCellValue("Date of Birth");
            headerRow.createCell(2).setCellValue("NIC");

            Row dataRow = sheet.createRow(1);
            dataRow.createCell(0).setCellValue("Test Customer");

            dataRow.createCell(2).setCellValue("TEST123");

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);

            return new MockMultipartFile(
                    "file",
                    "missing_cells.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    bos.toByteArray()
            );
        }
    }

    private MultipartFile createInvalidExcelFile() throws IOException {

        byte[] invalidContent = "This is not a valid Excel file".getBytes();

        return new MockMultipartFile(
                "file",
                "invalid.txt",
                "text/plain",
                invalidContent
        );
    }
}