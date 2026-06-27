package pl.tomskr.higienaharmonogrambackend.controller;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import pl.tomskr.higienaharmonogrambackend.entity.HgEmployee;
import pl.tomskr.higienaharmonogrambackend.entity.HgShifts;
import pl.tomskr.higienaharmonogrambackend.repository.HgEmployeeRepository;
import pl.tomskr.higienaharmonogrambackend.repository.HgShiftsRepository;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class HgShiftsControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HgShiftsRepository hgShiftsRepository;

    @Autowired
    private HgEmployeeRepository hgEmployeeRepository;

    private HgEmployee testEmployee;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        hgShiftsRepository.deleteAll();
        hgEmployeeRepository.deleteAll();

        testEmployee = HgEmployee.builder()
                .firstName("John")
                .lastName("Doe")
                .employee_Id("EMP001")
                .build();
        testEmployee = hgEmployeeRepository.save(testEmployee);
    }

    @Test
    void shouldReturnEmptyListWhenNoShiftsExist() throws Exception {
        mockMvc.perform(get("/api/shifts"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void shouldReturnAllShifts() throws Exception {
        HgShifts shift = HgShifts.builder()
                .employee(testEmployee)
                .shiftType('A')
                .shiftLength(8)
                .fullDate(LocalDate.now())
                .isHoliday(false)
                .build();
        hgShiftsRepository.save(shift);

        mockMvc.perform(get("/api/shifts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].shiftType").value("A"))
                .andExpect(jsonPath("$[0].shiftLength").value(8));
    }

    @Test
    void shouldReturnShiftById() throws Exception {
        HgShifts shift = HgShifts.builder()
                .employee(testEmployee)
                .shiftType('B')
                .shiftLength(12)
                .fullDate(LocalDate.now())
                .isHoliday(true)
                .build();
        HgShifts savedShift = hgShiftsRepository.save(shift);

        mockMvc.perform(get("/api/shifts/{id}", savedShift.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedShift.getId()))
                .andExpect(jsonPath("$.shiftType").value("B"))
                .andExpect(jsonPath("$.shiftLength").value(12));
    }

    @Test
    void shouldCreateShift() throws Exception {
        HgShifts shift = HgShifts.builder()
                .employee(testEmployee)
                .shiftType('C')
                .shiftLength(8)
                .fullDate(LocalDate.now())
                .isHoliday(false)
                .build();

        mockMvc.perform(post("/api/shifts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shift)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shiftType").value("C"))
                .andExpect(jsonPath("$.shiftLength").value(8));

        assertThat(hgShiftsRepository.findAll()).hasSize(1);
    }

    @Test
    void shouldUpdateShift() throws Exception {
        HgShifts shift = HgShifts.builder()
                .employee(testEmployee)
                .shiftType('A')
                .shiftLength(8)
                .fullDate(LocalDate.now())
                .isHoliday(false)
                .build();
        HgShifts savedShift = hgShiftsRepository.save(shift);

        HgShifts updatedDetails = HgShifts.builder()
                .employee(testEmployee)
                .shiftType('D')
                .shiftLength(10)
                .fullDate(LocalDate.now())
                .isHoliday(true)
                .build();

        mockMvc.perform(put("/api/shifts/{id}", savedShift.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shiftType").value("D"))
                .andExpect(jsonPath("$.shiftLength").value(10));

        HgShifts updatedShift = hgShiftsRepository.findById(savedShift.getId()).orElseThrow();
        assertThat(updatedShift.getShiftType()).isEqualTo('D');
        assertThat(updatedShift.getShiftLength()).isEqualTo(10);
    }

    @Test
    void shouldDeleteShift() throws Exception {
        HgShifts shift = HgShifts.builder()
                .employee(testEmployee)
                .shiftType('A')
                .shiftLength(8)
                .fullDate(LocalDate.now())
                .isHoliday(false)
                .build();
        HgShifts savedShift = hgShiftsRepository.save(shift);

        mockMvc.perform(delete("/api/shifts/{id}", savedShift.getId()))
                .andExpect(status().isNoContent());

        assertThat(hgShiftsRepository.findById(savedShift.getId())).isEmpty();
    }

    @Test
    void shouldFillShifts() throws Exception {
        HgShifts shift = HgShifts.builder()
                .employee(testEmployee)
                .shiftType('A')
                .shiftLength(8)
                .fullDate(LocalDate.now())
                .isHoliday(false)
                .build();
        hgShiftsRepository.save(shift);

        mockMvc.perform(post("/api/shifts/fill"))
                .andExpect(status().isOk());
    }
}
