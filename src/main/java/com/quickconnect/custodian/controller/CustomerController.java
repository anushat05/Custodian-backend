package com.quickconnect.custodian.controller;

import com.quickconnect.custodian.model.Customer;
import com.quickconnect.custodian.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;
import java.util.List;

/**
 * REST controller for managing customers.
 * Supports adding and retrieving customer records.
 */
@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * Adds customers to the system after validation.
     * @param customers List of incoming customer objects
     * @return Success or error message
     */
    @PostMapping
    public ResponseEntity<List<String>> addCustomers(@Valid @RequestBody List<Customer> customers) {
        List<String> result = customerService.addCustomers(customers);
        return ResponseEntity.ok(result);
    }

    /**
     * Returns the list of all customers stored in the system.
     * @return List of customers
     */
    @GetMapping
    public ResponseEntity<List<Customer>> getCustomers() {
        return ResponseEntity.ok(customerService.getCustomers());
    }
}

