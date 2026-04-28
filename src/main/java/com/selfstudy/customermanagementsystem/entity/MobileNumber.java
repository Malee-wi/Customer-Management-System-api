package com.selfstudy.customermanagementsystem.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class MobileNumber {
    @Id
    @GeneratedValue
    private Long id;

    private String number;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
}
