package pl.tomskr.higienaharmonogrambackend.controller;

import tools.jackson.databind.ObjectMapper;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class HgEmployeeControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

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

    /**
     * Tests the `getEmployeeById` method.
     */
    @Test
    void shouldReturnEmployeeById() throws Exception {
        HgEmployee employee = HgEmployee.builder()
                .firstName("Alice")
                .lastName("Wonderland")
                .employee_Id("EMP003")
                .build();
        HgEmployee savedEmployee = hgEmployeeRepository.save(employee);

        mockMvc.perform(get("/api/employees/{id}", savedEmployee.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(savedEmployee.getId()))
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.lastName").value("Wonderland"))
                .andExpect(jsonPath("$.employee_Id").value("EMP003"));
    }

    /**
     * Tests the `createEmployee` method.
     */
    @Test
    void shouldCreateEmployee() throws Exception {
        HgEmployee employee = HgEmployee.builder()
                .firstName("Bob")
                .lastName("Builder")
                .employee_Id("EMP004")
                .build();

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Bob"))
                .andExpect(jsonPath("$.lastName").value("Builder"))
                .andExpect(jsonPath("$.employee_Id").value("EMP004"));

        List<HgEmployee> employees = hgEmployeeRepository.findAll();
        assertThat(employees).anyMatch(e -> e.getFirstName().equals("Bob"));
    }

    /**
     * Tests the `updateEmployee` method.
     */
    @Test
    void shouldUpdateEmployee() throws Exception {
        HgEmployee employee = HgEmployee.builder()
                .firstName("Charlie")
                .lastName("Brown")
                .employee_Id("EMP005")
                .build();
        HgEmployee savedEmployee = hgEmployeeRepository.save(employee);

        HgEmployee updatedDetails = HgEmployee.builder()
                .firstName("Charles")
                .lastName("Grey")
                .employee_Id("EMP005-UPD")
                .build();

        mockMvc.perform(put("/api/employees/{id}", savedEmployee.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Charles"))
                .andExpect(jsonPath("$.lastName").value("Grey"))
                .andExpect(jsonPath("$.employee_Id").value("EMP005-UPD"));

        HgEmployee updatedEmployee = hgEmployeeRepository.findById(savedEmployee.getId()).orElseThrow();
        assertThat(updatedEmployee.getFirstName()).isEqualTo("Charles");
        assertThat(updatedEmployee.getLastName()).isEqualTo("Grey");
    }

    /**
     * Tests the `deleteEmployee` method.
     */
    @Test
    void shouldDeleteEmployee() throws Exception {
        HgEmployee employee = HgEmployee.builder()
                .firstName("Dave")
                .lastName("Dangerous")
                .employee_Id("EMP006")
                .build();
        HgEmployee savedEmployee = hgEmployeeRepository.save(employee);

        mockMvc.perform(delete("/api/employees/{id}", savedEmployee.getId()))
                .andExpect(status().isNoContent());

        assertThat(hgEmployeeRepository.findById(savedEmployee.getId())).isEmpty();
    }
}