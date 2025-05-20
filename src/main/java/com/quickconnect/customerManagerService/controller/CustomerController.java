package com.quickconnect.customerManagerService.controller;

import com.quickconnect.customerManagerService.exception.InvalidCustomerRequestException;
import com.quickconnect.customerManagerService.model.Customer;
import com.quickconnect.customerManagerService.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        List<String> errors = new ArrayList<>();

        if (customers.size() < 2) {
            errors.add("Request must contain at least 2 customers.");
        }

        for (int i = 1; i < customers.size(); i++) {
            if (customers.get(i).getId() <= customers.get(i - 1).getId()) {
                errors.add("Customer IDs must be in strictly increasing order.");
                break;
            }
        }

        if (!errors.isEmpty()) {
            throw new InvalidCustomerRequestException(errors);
        }

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

