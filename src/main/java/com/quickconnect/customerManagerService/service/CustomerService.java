package com.quickconnect.customerManagerService.service;

import com.quickconnect.customerManagerService.exception.CustomerServiceException;
import com.quickconnect.customerManagerService.model.Customer;
import com.quickconnect.customerManagerService.datastore.CustomerPersistence;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
public class CustomerService {
    private final List<Customer> customers = new ArrayList<>();
    private final Set<Integer> usedIds = new HashSet<>();
    private final CustomerPersistence persistence;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> pendingSave = null;
    private final int SAVE_DELAY_SECONDS = 3;

    public CustomerService(CustomerPersistence persistence) {
        this.persistence = persistence;
    }

    public void loadData() {
        try {
            List<Customer> loaded = persistence.load();
            if (loaded != null) {
                customers.clear();
                usedIds.clear();
                customers.addAll(loaded);
                for (Customer c : customers) {
                    usedIds.add(c.getId());
                }
                System.out.println("Loaded " + customers.size() + " customers from file.");
            }
        }catch (Exception e) {
            throw new CustomerServiceException("Failed to load customers from file", e);
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

        scheduleDebouncedSave();
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

    private void scheduleDebouncedSave() {
        if (pendingSave != null && !pendingSave.isDone()) {
            pendingSave.cancel(false); // Cancel previous if not yet started
        }

        pendingSave = scheduler.schedule(() -> {
            saveCustomersToFile();
            System.out.println("[Debounced Save] Data persisted to file");
        }, SAVE_DELAY_SECONDS, TimeUnit.SECONDS);
    }

    private void saveCustomersToFile() {
        try {
            persistence.save(customers);
        } catch (Exception e) {
            throw new CustomerServiceException("Failed to save customers to file", e);
        }

    }

    @PreDestroy
    public void onShutdown() {
        if (pendingSave != null && !pendingSave.isDone()) {
            pendingSave.cancel(false);
            saveCustomersToFile(); // Ensure save on shutdown
        }
        scheduler.shutdown();
    }

}
