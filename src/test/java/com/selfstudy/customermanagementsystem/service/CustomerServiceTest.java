package com.selfstudy.customermanagementsystem.service;

import com.selfstudy.customermanagementsystem.dto.*;
import com.selfstudy.customermanagementsystem.entity.Address;
import com.selfstudy.customermanagementsystem.entity.Customer;
import com.selfstudy.customermanagementsystem.entity.MobileNumber;
import com.selfstudy.customermanagementsystem.repository.CustomerRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepo customerRepo;

    @InjectMocks
    private CustomerService customerService;

    private CustomerRequestDTO customerRequestDTO;
    private Customer customer;
    private List<MobileNumberDTO> mobileNumberDTOs;
    private List<AddressDTO> addressDTOs;

    @BeforeEach
    void setUp() {

        mobileNumberDTOs = new ArrayList<>();
        MobileNumberDTO mobileDTO1 = new MobileNumberDTO();
        mobileDTO1.setNumber("+94771234567");
        MobileNumberDTO mobileDTO2 = new MobileNumberDTO();
        mobileDTO2.setNumber("+94772345678");
        mobileNumberDTOs.add(mobileDTO1);
        mobileNumberDTOs.add(mobileDTO2);


        addressDTOs = new ArrayList<>();
        AddressDTO addressDTO1 = new AddressDTO();
        addressDTO1.setLine1("123 Main St");
        addressDTO1.setLine2("Apt 4B");
        addressDTO1.setCity("Colombo");
        addressDTO1.setCountry("Sri Lanka");

        AddressDTO addressDTO2 = new AddressDTO();
        addressDTO2.setLine1("456 Park Ave");
        addressDTO2.setLine2(null);
        addressDTO2.setCity("Kandy");
        addressDTO2.setCountry("Sri Lanka");
        addressDTOs.add(addressDTO1);
        addressDTOs.add(addressDTO2);


        customerRequestDTO = new CustomerRequestDTO();
        customerRequestDTO.setName("John Doe");
        customerRequestDTO.setDob(LocalDate.of(1990, 5, 15));
        customerRequestDTO.setNic("199005150123");
        customerRequestDTO.setMobileNumbers(mobileNumberDTOs);
        customerRequestDTO.setAddresses(addressDTOs);


        customer = new Customer();
        customer.setId(1L);
        customer.setName("John Doe");
        customer.setDob(LocalDate.of(1990, 5, 15));
        customer.setNic("199005150123");


        List<MobileNumber> mobileNumbers = new ArrayList<>();
        MobileNumber mobile1 = new MobileNumber();
        mobile1.setId(1L);
        mobile1.setNumber("+94771234567");
        mobile1.setCustomer(customer);
        MobileNumber mobile2 = new MobileNumber();
        mobile2.setId(2L);
        mobile2.setNumber("+94772345678");
        mobile2.setCustomer(customer);
        mobileNumbers.add(mobile1);
        mobileNumbers.add(mobile2);
        customer.setMobileNumbers(mobileNumbers);


        List<Address> addresses = new ArrayList<>();
        Address address1 = new Address();
        address1.setId(1L);
        address1.setLine1("123 Main St");
        address1.setLine2("Apt 4B");
        address1.setCity("Colombo");
        address1.setCountry("Sri Lanka");
        address1.setCustomer(customer);
        Address address2 = new Address();
        address2.setId(2L);
        address2.setLine1("456 Park Ave");
        address2.setLine2(null);
        address2.setCity("Kandy");
        address2.setCountry("Sri Lanka");
        address2.setCustomer(customer);
        addresses.add(address1);
        addresses.add(address2);
        customer.setAddresses(addresses);
    }

    @Test
    void createCustomer_Success() {
        when(customerRepo.findByNic(anyString())).thenReturn(Optional.empty());
        when(customerRepo.save(any(Customer.class))).thenReturn(customer);

        CustomerResponseDTO result = customerService.create(customerRequestDTO);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("199005150123", result.getNic());
        assertEquals(2, result.getMobileNumbers().size());
        assertEquals(2, result.getAddresses().size());

        verify(customerRepo, times(1)).findByNic("199005150123");
        verify(customerRepo, times(1)).save(any(Customer.class));
    }

    @Test
    void createCustomer_DuplicateNic_ThrowsConflictException() {
        when(customerRepo.findByNic(anyString())).thenReturn(Optional.of(customer));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            customerService.create(customerRequestDTO);
        });

        assertEquals(409, exception.getStatusCode().value());
        assertEquals("NIC already exists", exception.getReason());

        verify(customerRepo, never()).save(any(Customer.class));
    }

    @Test
    void createCustomer_WithNullMobileNumbers_HandlesGracefully() {
        customerRequestDTO.setMobileNumbers(null);

        when(customerRepo.findByNic(anyString())).thenReturn(Optional.empty());
        when(customerRepo.save(any(Customer.class))).thenReturn(customer);

        CustomerResponseDTO result = customerService.create(customerRequestDTO);

        assertNotNull(result);
        verify(customerRepo, times(1)).save(any(Customer.class));
    }

    @Test
    void createCustomer_WithNullAddresses_HandlesGracefully() {
        customerRequestDTO.setAddresses(null);

        when(customerRepo.findByNic(anyString())).thenReturn(Optional.empty());
        when(customerRepo.save(any(Customer.class))).thenReturn(customer);

        CustomerResponseDTO result = customerService.create(customerRequestDTO);

        assertNotNull(result);
        verify(customerRepo, times(1)).save(any(Customer.class));
    }

    @Test
    void getCustomerById_Success() {
        when(customerRepo.findById(1L)).thenReturn(Optional.of(customer));

        CustomerResponseDTO result = customerService.getCustomerById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("199005150123", result.getNic());
        assertEquals(2, result.getMobileNumbers().size());
        assertEquals(2, result.getAddresses().size());

        verify(customerRepo, times(1)).findById(1L);
    }

    @Test
    void getCustomerById_NotFound_ThrowsNotFoundException() {
        Long nonExistentId = 999L;
        when(customerRepo.findById(nonExistentId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            customerService.getCustomerById(nonExistentId);
        });

        assertEquals(404, exception.getStatusCode().value());
        assertEquals("Customer not found with id: " + nonExistentId, exception.getReason());

        verify(customerRepo, times(1)).findById(nonExistentId);
    }

    @Test
    void getAllCustomers_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Customer> customers = Arrays.asList(customer);
        Page<Customer> customerPage = new PageImpl<>(customers, pageable, 1);

        when(customerRepo.findAll(pageable)).thenReturn(customerPage);

        Page<CustomerResponseDTO> result = customerService.getAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals("John Doe", result.getContent().get(0).getName());

        verify(customerRepo, times(1)).findAll(pageable);
    }

    @Test
    void getAllCustomers_EmptyPage_ReturnsEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Customer> emptyPage = new PageImpl<>(new ArrayList<>(), pageable, 0);

        when(customerRepo.findAll(pageable)).thenReturn(emptyPage);

        Page<CustomerResponseDTO> result = customerService.getAll(pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getContent().size());

        verify(customerRepo, times(1)).findAll(pageable);
    }

    @Test
    void updateCustomer_Success() {
        when(customerRepo.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepo.save(any(Customer.class))).thenReturn(customer);

        CustomerRequestDTO updateDTO = new CustomerRequestDTO();
        updateDTO.setName("John Updated");
        updateDTO.setDob(LocalDate.of(1990, 5, 15));
        updateDTO.setNic("199005150123");

        List<MobileNumberDTO> updatedMobiles = new ArrayList<>();
        MobileNumberDTO newMobile = new MobileNumberDTO();
        newMobile.setNumber("+94779999999");
        updatedMobiles.add(newMobile);
        updateDTO.setMobileNumbers(updatedMobiles);

        List<AddressDTO> updatedAddresses = new ArrayList<>();
        AddressDTO newAddress = new AddressDTO();
        newAddress.setLine1("New Address");
        newAddress.setLine2("New Line 2");
        newAddress.setCity("New City");
        newAddress.setCountry("New Country");
        updatedAddresses.add(newAddress);
        updateDTO.setAddresses(updatedAddresses);

        CustomerResponseDTO result = customerService.update(1L, updateDTO);

        assertNotNull(result);
        assertEquals("John Updated", result.getName());

        verify(customerRepo, times(1)).findById(1L);
        verify(customerRepo, times(1)).save(any(Customer.class));
    }

    @Test
    void updateCustomer_NotFound_ThrowsNotFoundException() {
        Long nonExistentId = 999L;
        when(customerRepo.findById(nonExistentId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            customerService.update(nonExistentId, customerRequestDTO);
        });

        assertEquals(404, exception.getStatusCode().value());
        assertEquals("Customer not found with id: " + nonExistentId, exception.getReason());

        verify(customerRepo, never()).save(any(Customer.class));
    }

    @Test
    void updateCustomer_ClearsAndReplacesMobileNumbers() {
        when(customerRepo.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepo.save(any(Customer.class))).thenReturn(customer);


        assertEquals(2, customer.getMobileNumbers().size());

        CustomerRequestDTO updateDTO = new CustomerRequestDTO();
        updateDTO.setName("John Doe");
        updateDTO.setDob(LocalDate.of(1990, 5, 15));
        updateDTO.setNic("199005150123");


        List<MobileNumberDTO> newMobiles = new ArrayList<>();
        MobileNumberDTO newMobile = new MobileNumberDTO();
        newMobile.setNumber("+94771111111");
        newMobiles.add(newMobile);
        updateDTO.setMobileNumbers(newMobiles);
        updateDTO.setAddresses(new ArrayList<>());

        customerService.update(1L, updateDTO);


        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepo).save(customerCaptor.capture());

        Customer savedCustomer = customerCaptor.getValue();
        assertEquals(1, savedCustomer.getMobileNumbers().size());
        assertEquals("+94771111111", savedCustomer.getMobileNumbers().get(0).getNumber());
    }

    @Test
    void updateCustomer_ClearsAndReplacesAddresses() {
        when(customerRepo.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepo.save(any(Customer.class))).thenReturn(customer);


        assertEquals(2, customer.getAddresses().size());

        CustomerRequestDTO updateDTO = new CustomerRequestDTO();
        updateDTO.setName("John Doe");
        updateDTO.setDob(LocalDate.of(1990, 5, 15));
        updateDTO.setNic("199005150123");
        updateDTO.setMobileNumbers(new ArrayList<>());


        List<AddressDTO> newAddresses = new ArrayList<>();
        AddressDTO newAddress = new AddressDTO();
        newAddress.setLine1("Only Address");
        newAddress.setLine2(null);
        newAddress.setCity("Only City");
        newAddress.setCountry("Only Country");
        newAddresses.add(newAddress);
        updateDTO.setAddresses(newAddresses);

        customerService.update(1L, updateDTO);


        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepo).save(customerCaptor.capture());

        Customer savedCustomer = customerCaptor.getValue();
        assertEquals(1, savedCustomer.getAddresses().size());
        assertEquals("Only Address", savedCustomer.getAddresses().get(0).getLine1());
    }

    @Test
    void createCustomer_WithEmptyMobileNumbers_HandlesCorrectly() {
        customerRequestDTO.setMobileNumbers(new ArrayList<>());

        when(customerRepo.findByNic(anyString())).thenReturn(Optional.empty());
        when(customerRepo.save(any(Customer.class))).thenReturn(customer);

        CustomerResponseDTO result = customerService.create(customerRequestDTO);

        assertNotNull(result);
        verify(customerRepo, times(1)).save(any(Customer.class));
    }

    @Test
    void createCustomer_WithEmptyAddresses_HandlesCorrectly() {
        customerRequestDTO.setAddresses(new ArrayList<>());

        when(customerRepo.findByNic(anyString())).thenReturn(Optional.empty());
        when(customerRepo.save(any(Customer.class))).thenReturn(customer);

        CustomerResponseDTO result = customerService.create(customerRequestDTO);

        assertNotNull(result);
        verify(customerRepo, times(1)).save(any(Customer.class));
    }
}