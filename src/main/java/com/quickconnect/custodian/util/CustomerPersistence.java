package com.quickconnect.custodian.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickconnect.custodian.model.Customer;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Component
public class CustomerPersistence {

    private final String filePath;
    private final ObjectMapper mapper = new ObjectMapper();

    public CustomerPersistence() {
        this(System.getProperty("user.dir") + "/data/customers.json");
    }

    public CustomerPersistence(String filePath) {
        this.filePath = filePath;
    }

    public void save(List<Customer> customers) {
        try {
            Files.createDirectories(Paths.get(filePath).getParent());
            File tempFile = new File(filePath + ".tmp");
            mapper.writerWithDefaultPrettyPrinter().writeValue(tempFile, customers);
            Files.move(tempFile.toPath(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Customer> load() {
        try {
            File file = new File(filePath);
            if (!file.exists()) return null;
            return mapper.readValue(file, new TypeReference<>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}