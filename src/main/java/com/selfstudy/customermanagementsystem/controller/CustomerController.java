package com.selfstudy.customermanagementsystem.controller;

import com.selfstudy.customermanagementsystem.dto.CustomerRequestDTO;
import com.selfstudy.customermanagementsystem.dto.CustomerResponseDTO;
import com.selfstudy.customermanagementsystem.service.CustomerService;
import com.selfstudy.customermanagementsystem.service.ExcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;
    private final ExcelService excelService;

    @PostMapping
    public CustomerResponseDTO create(@RequestBody CustomerRequestDTO customer) {
        return service.create(customer);
    }

    @GetMapping("/{id}")
    public CustomerResponseDTO get(@PathVariable Long id) {
        return service.getCustomerById(id);
    }

    @GetMapping
    public Page<CustomerResponseDTO> getAll(Pageable pageable) {
        return service.getAll(pageable);
    }

    @PutMapping("/{id}")
    public CustomerResponseDTO update(@PathVariable Long id, @RequestBody CustomerRequestDTO customer) {
        return service.update(id, customer);
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file) throws Exception {
        excelService.processExcel(file);
        return "Uploaded successfully";
    }
}
