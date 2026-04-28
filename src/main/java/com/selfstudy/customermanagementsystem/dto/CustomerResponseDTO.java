package com.selfstudy.customermanagementsystem.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CustomerResponseDTO {
    private Long id;
    private String name;
    private LocalDate dob;
    private String nic;

    private List<String> mobileNumbers;
    private List<AddressDTO> addresses;
}