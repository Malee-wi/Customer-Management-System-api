package com.selfstudy.customermanagementsystem.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String line1;
    private String line2;

    private String city;
    private String country;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
}
