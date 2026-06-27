package pl.tomskr.higienaharmonogrambackend.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pl.tomskr.higienaharmonogrambackend.entity.HgEmployee;
import pl.tomskr.higienaharmonogrambackend.entity.HgShifts;
import pl.tomskr.higienaharmonogrambackend.repository.HgEmployeeRepository;
import pl.tomskr.higienaharmonogrambackend.repository.HgShiftsRepository;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class HgEmployeeServiceTest {

    @Autowired
    private HgEmployeeService hgEmployeeService;

    @Autowired
    private HgEmployeeRepository hgEmployeeRepository;

    @Autowired
    private HgShiftsRepository hgShiftsRepository;

    @Test
    void shouldGenerateEmployeeIdIfEmpty() {
        // Given
        HgEmployee employee = HgEmployee.builder()
                .firstName("John")
                .lastName("Doe")
                // employee_Id is null
                .build();

        // When
        HgEmployee savedEmployee = hgEmployeeService.createEmployee(employee);

        // Then
        assertThat(savedEmployee.getEmployee_Id()).isNotNull();
        assertThat(savedEmployee.getEmployee_Id()).isNotEmpty();
        assertThat(savedEmployee.getEmployee_Id()).startsWith("EMP-");
    }

    @Test
    void shouldNotOverwriteEmployeeIdIfProvided() {
        // Given
        String providedId = "EMP999";
        HgEmployee employee = HgEmployee.builder()
                .firstName("Jane")
                .lastName("Smith")
                .employee_Id(providedId)
                .build();

        // When
        HgEmployee savedEmployee = hgEmployeeService.createEmployee(employee);

        // Then
        assertThat(savedEmployee.getEmployee_Id()).isEqualTo(providedId);
    }

    @Test
    void shouldDeleteEmployeeAndAssociatedShifts() {
        // Given
        HgEmployee employee = HgEmployee.builder()
                .firstName("John")
                .lastName("Doe")
                .employee_Id("EMP-DELETE-TEST")
                .build();
        HgEmployee savedEmployee = hgEmployeeRepository.save(employee);
        Long employeeId = savedEmployee.getId();

        HgShifts shift1 = HgShifts.builder()
                .employee(savedEmployee)
                .fullDate(LocalDate.now())
                .isHoliday(false)
                .build();
        HgShifts shift2 = HgShifts.builder()
                .employee(savedEmployee)
                .fullDate(LocalDate.now().plusDays(1))
                .isHoliday(false)
                .build();
        hgShiftsRepository.save(shift1);
        hgShiftsRepository.save(shift2);

        // Verify shifts exist
        assertThat(hgShiftsRepository.findAll()).anyMatch(s -> s.getEmployee().getId().equals(employeeId));

        // When
        hgEmployeeService.deleteEmployee(employeeId);

        // Then
        assertThat(hgEmployeeRepository.findById(employeeId)).isEmpty();
        assertThat(hgShiftsRepository.findAll()).noneMatch(s -> s.getEmployee().getId().equals(employeeId));
    }
}
