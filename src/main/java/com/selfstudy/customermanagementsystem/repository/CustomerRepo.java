package com.selfstudy.customermanagementsystem.repository;

import com.selfstudy.customermanagementsystem.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepo extends JpaRepository<Customer, Long> {
    Optional<Customer> findByNic(String nic);
    boolean existsByNic(String nic);

}
