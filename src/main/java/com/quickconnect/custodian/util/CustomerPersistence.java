package com.quickconnect.custodian.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickconnect.custodian.model.Customer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class CustomerPersistence {
    private static final String DATA_DIR = System.getProperty("user.dir") + "/data";
    private static final String FILE_PATH = DATA_DIR + "/customers.json";
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void save(List<Customer> customers) {
        try {
            Files.createDirectories(Paths.get(DATA_DIR)); // Ensure /data exists

            // Atomic save
            File tempFile = new File(FILE_PATH + ".tmp");
            mapper.writerWithDefaultPrettyPrinter().writeValue(tempFile, customers);

            Files.move(tempFile.toPath(), Paths.get(FILE_PATH), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace(); // Consider logging in real projects
        }
    }

    public static List<Customer> load() {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) return null;

            return mapper.readValue(file, new TypeReference<List<Customer>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

