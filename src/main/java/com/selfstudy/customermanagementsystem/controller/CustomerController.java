package com.selfstudy.customermanagementsystem.controller;

import com.selfstudy.customermanagementsystem.dto.CustomerRequestDTO;
import com.selfstudy.customermanagementsystem.dto.CustomerResponseDTO;
import com.selfstudy.customermanagementsystem.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;

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
}
