package com.quickconnect.customerManagerService.service;

import com.quickconnect.customerManagerService.datastore.DynamoCustomerPersistence;
import com.quickconnect.customerManagerService.model.Customer;
import com.quickconnect.customerManagerService.datastore.CustomerPersistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomerServiceTest {

    private CustomerService customerService;
    private CustomerPersistence mockPersistence;

    @BeforeEach
    void setup() {
        mockPersistence = Mockito.mock(DynamoCustomerPersistence.class);
        Mockito.when(mockPersistence.load()).thenReturn(List.of());

        customerService = new CustomerService();
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(mockPersistence);
    }


    @Test
    void testAddCustomer_validCustomer_shouldAddAndSort() {
        Customer c1 = new Customer("Leia", "Ray", 25, 1);
        Customer c2 = new Customer("Frank", "Anderson", 30, 2);

        customerService.addCustomers(List.of(c1, c2));
        List<Customer> result = customerService.getCustomers();
        try {
            Thread.sleep(4000); // wait for > SAVE_DELAY_SECONDS (3 sec)
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupt status
            throw new RuntimeException("Test interrupted", e);
        }
        assertEquals(2, result.size());
        assertEquals("Anderson", result.get(0).getLastName());
        assertEquals("Ray", result.get(1).getLastName());
//        Mockito.verify(mockPersistence).save(Mockito.any());
    }

    @Test
    void testAddCustomer_duplicateId_shouldSkip() {
        Customer c1 = new Customer("Leia", "Ray", 25, 1);
        Customer c2 = new Customer("Frank", "Chan", 22, 1); // duplicate ID

        List<String> response = customerService.addCustomers(List.of(c1, c2));
        assertEquals(2, response.size());
        assertTrue(response.get(1).contains("already exists"));
        assertEquals(1, customerService.getCustomers().size());
    }

//    @Test
    void testAddCustomers_sortingByLastAndFirstName() {
        Customer c1 = new Customer("Leia", "Ray", 25, 1);
        Customer c2 = new Customer("Adam", "Ray", 26, 2); // same last, earlier first
        Customer c3 = new Customer("Zoe", "Anderson", 27, 3);

        customerService.addCustomers(List.of(c1, c2, c3));
        List<Customer> result = customerService.getCustomers();

        assertEquals(3, result.size());
        assertEquals("Anderson", result.get(0).getLastName());
        assertEquals("Adam", result.get(1).getFirstName());
        assertEquals("Leia", result.get(2).getFirstName());
    }

}
