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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class HgShiftsServiceFillTest {

    @Autowired
    private HgShiftsService hgShiftsService;

    @Autowired
    private HgEmployeeRepository hgEmployeeRepository;

    @Autowired
    private HgShiftsRepository hgShiftsRepository;

    @Test
    void shouldFillShiftsForAllEmployeesInMonthRange() {
        // Given
        HgEmployee emp1 = hgEmployeeRepository.save(HgEmployee.builder().firstName("E1").lastName("L1").employee_Id("E1").build());
        HgEmployee emp2 = hgEmployeeRepository.save(HgEmployee.builder().firstName("E2").lastName("L2").employee_Id("E2").build());

        // Earliest shift: 2026-02-15
        hgShiftsRepository.save(HgShifts.builder().employee(emp1).fullDate(LocalDate.of(2026, 2, 15)).shiftType('B').shiftLength(8).build());
        // Latest shift: 2026-04-10
        hgShiftsRepository.save(HgShifts.builder().employee(emp2).fullDate(LocalDate.of(2026, 4, 10)).shiftType('C').shiftLength(8).build());

        // When
        hgShiftsService.fillShifts();

        // Then
        // Month range: February (28 days), March (31 days), April (30 days) = 89 days
        List<HgEmployee> allEmployees = hgEmployeeRepository.findAll();
        int expectedShiftsCount = 89 * allEmployees.size();
        List<HgShifts> allShifts = hgShiftsRepository.findAll();
        assertThat(allShifts).hasSize(expectedShiftsCount);

        // Verify shift for emp1 on April 30 (end of range)
        assertThat(hgShiftsRepository.existsByEmployeeIdAndFullDate(emp1.getId(), LocalDate.of(2026, 4, 30))).isTrue();
        // Verify shift for emp2 on Feb 1 (start of range)
        assertThat(hgShiftsRepository.existsByEmployeeIdAndFullDate(emp2.getId(), LocalDate.of(2026, 2, 1))).isTrue();
        
        // Verify original shifts were not changed
        HgShifts originalShift1 = hgShiftsRepository.findAll().stream()
                .filter(s -> s.getEmployee().getId().equals(emp1.getId()) && s.getFullDate().equals(LocalDate.of(2026, 2, 15)))
                .findFirst().orElseThrow();
        assertThat(originalShift1.getShiftType()).isEqualTo('B');
    }

    @Test
    void shouldDoNothingWhenNoShiftsExist() {
        // Given
        hgShiftsRepository.deleteAll(); // Clear shifts but keep employees

        // When
        hgShiftsService.fillShifts();

        // Then
        assertThat(hgShiftsRepository.findAll()).isEmpty();
    }
}
