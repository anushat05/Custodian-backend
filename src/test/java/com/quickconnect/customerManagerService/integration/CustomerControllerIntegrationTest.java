package com.quickconnect.customerManagerService.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickconnect.customerManagerService.config.TestPersistenceConfig;
import com.quickconnect.customerManagerService.datastore.CustomerPersistence;
import com.quickconnect.customerManagerService.model.Customer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestPersistenceConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CustomerControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean
    private CustomerPersistence customerPersistence;

    @BeforeEach
    void reset() {
        customerPersistence.save(new ArrayList<>());
    }

//    @Test
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
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0]").value("Accepted: ID 1001"))
                .andExpect(jsonPath("$[1]").value("Accepted: ID 1002"));
    }

//    @Test
    @Order(2)
    void testGetCustomers_shouldReturnSortedList() throws Exception {
        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("Adam")) // sorted by lastName then firstName
                .andExpect(jsonPath("$[1].firstName").value("Leia"));
    }

//    @Test
    @Order(3)
    void testAddCustomers_invalidAge_shouldFailValidation() throws Exception {
        List<Customer> input = List.of(
                new Customer("John", "Doe", 17, 1003)
        );

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0]").value("Rejected: ID 1003 is under 18."));
    }

//    @Test
    @Order(4)
    void testAddCustomers_duplicateId_shouldFailValidation() throws Exception {
        List<Customer> input = List.of(
                new Customer("Zoe", "Ray", 28, 1002) // ID 1002 already added
        );

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0]").value("Rejected: ID 1002 already exists."));
    }
}
