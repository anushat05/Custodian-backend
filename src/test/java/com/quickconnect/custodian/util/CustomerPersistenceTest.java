package com.quickconnect.custodian.util;

import com.quickconnect.custodian.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.io.TempDir;

class CustomerPersistenceTest {

    private CustomerPersistence persistence;

    @BeforeEach
    void setup(@TempDir Path tempDir) {
        File testFile = tempDir.resolve("customers.json").toFile();
        persistence = new CustomerPersistence(testFile.getAbsolutePath());
    }

    @Test
    void testSaveAndLoadCustomers() {
        List<Customer> input = List.of(
                new Customer("Leia", "Ray", 25, 101),
                new Customer("Frank", "Anderson", 30, 102)
        );

        persistence.save(input);
        List<Customer> result = persistence.load();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Leia", result.get(0).getFirstName());
    }

    @Test
    void testLoadFromMissingFileReturnsNull() {
        List<Customer> result = persistence.load();
        assertNull(result);
    }
}
