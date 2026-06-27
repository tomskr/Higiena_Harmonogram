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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class HgShiftsServiceTest {

    @Autowired
    private HgShiftsService hgShiftsService;

    @Autowired
    private HgEmployeeRepository hgEmployeeRepository;

    @Autowired
    private HgShiftsRepository hgShiftsRepository;

    @Test
    void shouldCreateShiftWithValidEmployee() {
        // Given
        HgEmployee employee = HgEmployee.builder()
                .firstName("John")
                .lastName("Doe")
                .employee_Id("EMP123")
                .build();
        HgEmployee savedEmployee = hgEmployeeRepository.save(employee);

        HgShifts shift = HgShifts.builder()
                .employee(HgEmployee.builder().id(savedEmployee.getId()).build())
                .fullDate(LocalDate.now())
                .isHoliday(false)
                .build();

        // When
        HgShifts createdShift = hgShiftsService.createShift(shift);

        // Then
        assertThat(createdShift.getId()).isNotNull();
        assertThat(createdShift.getEmployee().getFirstName()).isEqualTo("John");
        assertThat(createdShift.getEmployee().getLastName()).isEqualTo("Doe");
    }

    @Test
    void shouldThrowExceptionWhenEmployeeNotFound() {
        // Given
        HgShifts shift = HgShifts.builder()
                .employee(HgEmployee.builder().id(999L).build())
                .fullDate(LocalDate.now())
                .isHoliday(false)
                .build();

        // When & Then
        assertThatThrownBy(() -> hgShiftsService.createShift(shift))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Employee not found with id: 999");
    }

    @Test
    void shouldThrowExceptionWhenEmployeeIdNotProvided() {
        // Given
        HgShifts shift = HgShifts.builder()
                .employee(HgEmployee.builder().build())
                .fullDate(LocalDate.now())
                .isHoliday(false)
                .build();

        // When & Then
        assertThatThrownBy(() -> hgShiftsService.createShift(shift))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Employee ID must be provided");
    }
}
