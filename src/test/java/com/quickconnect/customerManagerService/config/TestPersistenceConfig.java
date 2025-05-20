package com.quickconnect.customerManagerService.config;

import com.quickconnect.customerManagerService.datastore.CustomerPersistence;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
public class TestPersistenceConfig {

    @Bean
    public CustomerPersistence customerPersistence() throws Exception {
        Path tempFile = Files.createTempFile("customers-test-", ".json");
        return new CustomerPersistence(tempFile.toString());
    }
}
