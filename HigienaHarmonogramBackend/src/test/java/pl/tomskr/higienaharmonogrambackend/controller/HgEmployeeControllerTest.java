package pl.tomskr.higienaharmonogrambackend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import pl.tomskr.higienaharmonogrambackend.entity.HgEmployee;
import pl.tomskr.higienaharmonogrambackend.repository.HgEmployeeRepository;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class HgEmployeeControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Autowired
    private HgEmployeeRepository hgEmployeeRepository;

    /**
     * Tests the scenario where the database has no employees.
     * Ensures that the `getAllEmployees` method returns an empty array.
     */
    @Test
    void shouldReturnEmptyListWhenNoEmployeesExist() throws Exception {
        // Ensure the database is empty
        hgEmployeeRepository.deleteAll();

        // Perform the GET request
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    /**
     * Tests the scenario where the database contains multiple employees.
     * Ensures that the `getAllEmployees` method returns the expected employees.
     */
    @Test
    void shouldReturnAllEmployees() throws Exception {
        // Set up test data
        HgEmployee employee1 = HgEmployee.builder()
                .firstName("John")
                .lastName("Doe")
                .employee_Id("EMP001")
                .build();
        HgEmployee employee2 = HgEmployee.builder()
                .firstName("Jane")
                .lastName("Smith")
                .employee_Id("EMP002")
                .build();
        hgEmployeeRepository.deleteAll(); // Clear any existing data
        hgEmployeeRepository.saveAll(List.of(employee1, employee2)); // Save test data

        // Perform the GET request
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(
                        "[" +
                                "{\"id\":" + employee1.getId() + ",\"firstName\":\"John\",\"lastName\":\"Doe\",\"employee_Id\":\"EMP001\"}," +
                                "{\"id\":" + employee2.getId() + ",\"firstName\":\"Jane\",\"lastName\":\"Smith\",\"employee_Id\":\"EMP002\"}" +
                                "]"
                ));

        // Verify repository contains expected data
        assertThat(hgEmployeeRepository.findAll()).hasSize(2);
    }
}