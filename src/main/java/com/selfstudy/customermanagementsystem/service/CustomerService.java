package com.selfstudy.customermanagementsystem.service;

import com.selfstudy.customermanagementsystem.dto.CustomerRequestDTO;
import com.selfstudy.customermanagementsystem.dto.CustomerResponseDTO;
import com.selfstudy.customermanagementsystem.entity.Address;
import com.selfstudy.customermanagementsystem.entity.Customer;
import com.selfstudy.customermanagementsystem.entity.MobileNumber;
import com.selfstudy.customermanagementsystem.mapper.CustomerMapper;
import com.selfstudy.customermanagementsystem.repository.CustomerRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepo repo;


    public CustomerResponseDTO create(CustomerRequestDTO dto) {

        repo.findByNic(dto.getNic()).ifPresent(x -> {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "NIC already exists"
            );
        });

        Customer customer = CustomerMapper.toEntity(dto);

        Customer saved = repo.save(customer);

        return CustomerMapper.toDTO(saved);
    }


    @Transactional
    public CustomerResponseDTO getCustomerById(Long id) {

        Customer customer = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Customer not found with id: " + id
                ));

        return CustomerMapper.toDTO(customer);
    }


    public Page<CustomerResponseDTO> getAll(Pageable pageable) {

        return repo.findAll(pageable)
                .map(CustomerMapper::toDTO);
    }


    @Transactional
    public CustomerResponseDTO update(Long id, CustomerRequestDTO dto) {
        Customer existing = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Customer not found with id: " + id
                ));


        existing.setName(dto.getName());
        existing.setDob(dto.getDob());
        existing.setNic(dto.getNic());


        existing.getMobileNumbers().clear();
        if (dto.getMobileNumbers() != null) {
            dto.getMobileNumbers().forEach(mobileDTO -> {
                MobileNumber mobile = new MobileNumber();
                mobile.setNumber(mobileDTO.getNumber());
                mobile.setCustomer(existing);
                existing.getMobileNumbers().add(mobile);
            });
        }


        existing.getAddresses().clear();
        if (dto.getAddresses() != null) {
            dto.getAddresses().forEach(addressDTO -> {
                Address address = new Address();
                address.setLine1(addressDTO.getLine1());
                address.setLine2(addressDTO.getLine2());
                address.setCity(addressDTO.getCity());
                address.setCountry(addressDTO.getCountry());
                address.setCustomer(existing);
                existing.getAddresses().add(address);
            });
        }

        return CustomerMapper.toDTO(repo.save(existing));
    }

}