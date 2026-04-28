package com.selfstudy.customermanagementsystem.service;

import com.selfstudy.customermanagementsystem.entity.Customer;
import com.selfstudy.customermanagementsystem.repository.CustomerRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepo repo;

    public Customer create(Customer customer){
        repo.findByNic(customer.getNic()).ifPresent(x->{
            throw new RuntimeException("NIC already exists");
        });

        return repo.save(customer);
    }

    @Transactional
    public Customer getCustomerById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND,
                        "Customer not found with id: " + id
                ));
    }

    public Page<Customer> getAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    public Customer update(Long id, Customer updateCustomer) {
        Customer existingCustomer = repo.findById(id)
                .orElseThrow(() ->  new ResponseStatusException(
                        NOT_FOUND,
                        "Customer not found with id: " + id));

        existingCustomer.setName(updateCustomer.getName());
        existingCustomer.setDob(updateCustomer.getDob());

        return repo.save(existingCustomer);
    }
}
