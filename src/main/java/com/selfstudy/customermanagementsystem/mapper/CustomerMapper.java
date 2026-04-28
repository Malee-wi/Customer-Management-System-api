package com.selfstudy.customermanagementsystem.mapper;

import com.selfstudy.customermanagementsystem.dto.*;
import com.selfstudy.customermanagementsystem.entity.*;

import java.util.stream.Collectors;

public class CustomerMapper {


    public static CustomerResponseDTO toDTO(Customer customer) {

        CustomerResponseDTO dto = new CustomerResponseDTO();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setDob(customer.getDob());
        dto.setNic(customer.getNic());

        dto.setMobileNumbers(
                customer.getMobileNumbers()
                        .stream()
                        .map(MobileNumber::getNumber)
                        .collect(Collectors.toList())
        );

        dto.setAddresses(
                customer.getAddresses()
                        .stream()
                        .map(a -> {
                            AddressDTO ad = new AddressDTO();
                            ad.setLine1(a.getLine1());
                            ad.setLine2(a.getLine2());
                            ad.setCity(a.getCity());
                            ad.setCountry(a.getCountry());
                            return ad;
                        })
                        .toList()
        );

        return dto;
    }


    public static Customer toEntity(CustomerRequestDTO dto) {

        Customer customer = new Customer();
        customer.setName(dto.getName());
        customer.setDob(dto.getDob());
        customer.setNic(dto.getNic());

        if (dto.getMobileNumbers() != null) {
            customer.setMobileNumbers(
                    dto.getMobileNumbers().stream().map(m -> {
                        MobileNumber mn = new MobileNumber();
                        mn.setNumber(m.getNumber());
                        mn.setCustomer(customer);
                        return mn;
                    }).collect(Collectors.toList())
            );
        }

        if (dto.getAddresses() != null) {
            customer.setAddresses(
                    dto.getAddresses().stream().map(a -> {
                        Address ad = new Address();
                        ad.setLine1(a.getLine1());
                        ad.setLine2(a.getLine2());
                        ad.setCity(a.getCity());
                        ad.setCountry(a.getCountry());
                        ad.setCustomer(customer);
                        return ad;
                    }).collect(Collectors.toList())
            );
        }

        return customer;
    }
}