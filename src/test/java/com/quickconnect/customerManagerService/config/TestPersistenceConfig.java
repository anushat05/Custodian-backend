package com.quickconnect.customerManagerService.config;

import com.quickconnect.customerManagerService.datastore.CustomerPersistence;
import com.quickconnect.customerManagerService.model.Customer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class TestPersistenceConfig {

    private final List<Customer> customerStore = new ArrayList<>();

    @Primary
    @Bean
    public CustomerPersistence customerPersistence() {
        return new CustomerPersistence() {
            @Override
            public List<Customer> load() {
                return new ArrayList<>(customerStore); // simulate load
            }

            @Override
            public void save(List<Customer> customers) {
                customerStore.clear();
                customerStore.addAll(customers); // simulate save
            }
        };
    }
}
