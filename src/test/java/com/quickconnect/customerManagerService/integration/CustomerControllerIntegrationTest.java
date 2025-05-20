package com.quickconnect.customerManagerService.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickconnect.customerManagerService.config.TestPersistenceConfig;
import com.quickconnect.customerManagerService.model.Customer;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.util.List;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestPersistenceConfig.class)  // override production bean
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CustomerControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private static final File file = new File(System.getProperty("user.dir") + "/data/customers.json");

    @Test
    @Order(1)
    void testAddCustomers_validInput_shouldReturnSuccess() throws Exception {
        List<Customer> input = List.of(
                new Customer("Leia", "Ray", 25, 1001),
                new Customer("Adam", "Ray", 22, 1002)
        );

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @Order(2)
    void testGetCustomers_shouldReturnSortedList() throws Exception {
        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("Adam"));
    }

    @Test
    @Order(3)
    void testAddCustomers_invalidAge_shouldFailValidation() throws Exception {
        List<Customer> input = List.of(
                new Customer("John", "Doe", 17, 1003)
        );

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());
    }
}
