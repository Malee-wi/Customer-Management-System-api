package com.selfstudy.customermanagementsystem.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Address {

    @Id
    @GeneratedValue
    private Long id;

    private String line1;
    private String line2;

    private String city;
    private String country;

    @ManyToOne
    private Customer customer;
}
