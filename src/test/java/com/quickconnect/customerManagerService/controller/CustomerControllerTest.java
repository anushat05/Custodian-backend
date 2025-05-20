package com.quickconnect.customerManagerService.controller;

import com.quickconnect.customerManagerService.model.Customer;
import com.quickconnect.customerManagerService.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomerControllerTest {

    private CustomerController controller;
    private CustomerService mockService;

    @BeforeEach
    void setup() {
        mockService = Mockito.mock(CustomerService.class);
        controller = new CustomerController(mockService);
    }

    @Test
    void testAddCustomers_returnsSuccessResponse() {
        List<Customer> input = List.of(
                new Customer("Leia", "Ray", 25, 1),
                new Customer("Frank", "Anderson", 30, 2)
        );

        List<String> expectedResponse = List.of(
                "Customer ID 1 added.",
                "Customer ID 2 added."
        );

        when(mockService.addCustomers(input)).thenReturn(expectedResponse);

        ResponseEntity<List<String>> response = controller.addCustomers(input);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedResponse, response.getBody());
        verify(mockService).addCustomers(input);  // Ensure the service was called
    }

    @Test
    void testGetCustomers_returnsCustomerList() {
        List<Customer> data = List.of(new Customer("Leia", "Ray", 25, 1));

        when(mockService.getCustomers()).thenReturn(data);

        ResponseEntity<List<Customer>> response = controller.getCustomers();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(data, response.getBody());
    }

}
