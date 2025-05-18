package com.quickconnect.custodian.service;

import com.quickconnect.custodian.model.Customer;
import com.quickconnect.custodian.util.CustomerPersistence;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CustomerService {
    private final List<Customer> customers = new ArrayList<>();
    private final Set<Integer> usedIds = new HashSet<>();
    private final CustomerPersistence persistence;

    public CustomerService(CustomerPersistence persistence) {
        this.persistence = persistence;
        List<Customer> loaded = persistence.load();
        if (loaded != null) {
            customers.addAll(loaded);
            for (Customer c : customers) {
                usedIds.add(c.getId());
            }
        }
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public List<String> addCustomers(List<Customer> newCustomers) {
        List<String> responses = new ArrayList<>();

        for (Customer c : newCustomers) {
            if (usedIds.contains(c.getId())) {
                responses.add("ID " + c.getId() + " already exists. Skipping.");
                continue;
            }

            // Manual insert logic based on lastName then firstName
            insertInOrder(c);
            usedIds.add(c.getId());
            responses.add("Customer ID " + c.getId() + " added.");
        }

        this.persistence.save(customers);
        return responses;
    }

    private void insertInOrder(Customer newCustomer) {
        int i = 0;
        while (i < customers.size()) {
            Customer existing = customers.get(i);
            int lastCompare = newCustomer.getLastName().compareToIgnoreCase(existing.getLastName());
            if (lastCompare < 0) break;
            if (lastCompare == 0 && newCustomer.getFirstName().compareToIgnoreCase(existing.getFirstName()) < 0)
                break;
            i++;
        }
        customers.add(i, newCustomer);
    }
}
