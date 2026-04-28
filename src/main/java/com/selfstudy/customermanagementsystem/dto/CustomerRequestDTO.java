package com.selfstudy.customermanagementsystem.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CustomerRequestDTO {
    private String name;
    private LocalDate dob;
    private String nic;

    private List<MobileNumberDTO> mobileNumbers;
    private List<AddressDTO> addresses;
}