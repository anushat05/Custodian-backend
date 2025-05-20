package com.quickconnect.customerManagerService.service;

import com.quickconnect.customerManagerService.datastore.DynamoCustomerPersistence;
import com.quickconnect.customerManagerService.datastore.CustomerPersistence;
import com.quickconnect.customerManagerService.exception.CustomerServiceException;
import com.quickconnect.customerManagerService.model.Customer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CustomerService {

    private final List<Customer> customers = new ArrayList<>();
    private final Set<Integer> usedIds = new HashSet<>();
    private final DynamoCustomerPersistence dynamoDBPersistence = new DynamoCustomerPersistence();

    @PostConstruct
    public void init() {
        loadData(); // Load persisted customers into memory
    }

    public void loadData() {
        try {
            List<Customer> loaded = dynamoDBPersistence.load();
            customers.clear();
            usedIds.clear();
            for (Customer c : loaded) {
                insertInOrder(c);
                usedIds.add(c.getId());
            }
            System.out.println("Loaded and inserted " + customers.size() + " customers from DynamoDB");
        } catch (Exception e) {
            throw new CustomerServiceException("Failed to load customers from DynamoDB", e);
        }
    }

    public List<Customer> getCustomers() {
        return customers; // already sorted manually via insertInOrder
    }

    public List<String> addCustomers(List<Customer> newCustomers) {
        List<String> responses = new ArrayList<>();

        for (Customer c : newCustomers) {
            if (c.getFirstName() == null || c.getLastName() == null || c.getId() == null) {
                responses.add("Missing required fields for ID: " + c.getId());
                continue;
            }

            if (c.getAge() < 18) {
                responses.add("Rejected: ID " + c.getId() + " is under 18.");
                continue;
            }

            if (usedIds.contains(c.getId())) {
                responses.add("Rejected: ID " + c.getId() + " already exists.");
                continue;
            }

            insertInOrder(c);
            usedIds.add(c.getId());
            responses.add("Accepted: ID " + c.getId());
        }

        try {
            dynamoDBPersistence.save(customers); // persist updated list
            System.out.println("Saved " + customers.size() + " customers to DynamoDB");
        } catch (Exception e) {
            throw new CustomerServiceException("Failed to save customers to DynamoDB", e);
        }

        return responses;
    }

    private void insertInOrder(Customer newCustomer) {
        int i = 0;
        while (i < customers.size()) {
            Customer existing = customers.get(i);
            int lastCompare = newCustomer.getLastName().compareToIgnoreCase(existing.getLastName());
            if (lastCompare < 0) break;
            if (lastCompare == 0 &&
                    newCustomer.getFirstName().compareToIgnoreCase(existing.getFirstName()) < 0)
                break;
            i++;
        }
        customers.add(i, newCustomer);
    }

    @PreDestroy
    public void onShutdown() {
        try {
            dynamoDBPersistence.save(customers); // save one last time
            System.out.println("Saved customers to DynamoDB on shutdown.");
        } catch (Exception e) {
            System.err.println("Failed to save on shutdown: " + e.getMessage());
        }
    }
}
