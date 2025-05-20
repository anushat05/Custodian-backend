package com.quickconnect.customerManagerService.config;

import com.quickconnect.customerManagerService.service.CustomerService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CustomerService customerService;

    public DataInitializer(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public void run(String... args) {
        customerService.loadData();
        System.out.println("✅ Customers loaded on application startup");
    }
}

