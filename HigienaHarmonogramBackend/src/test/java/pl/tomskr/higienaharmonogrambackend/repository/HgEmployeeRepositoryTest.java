package pl.tomskr.higienaharmonogrambackend.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pl.tomskr.higienaharmonogrambackend.entity.HgEmployee;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class HgEmployeeRepositoryTest {

    @Autowired
    private HgEmployeeRepository hgEmployeeRepository;

    @Test
    void shouldAddAndRemoveHgEmployee() {
        // Given
        HgEmployee employee = HgEmployee.builder()
                .firstName("John")
                .lastName("Doe")
                .employeeId("EMP123")
                .build();

        // When: Add
        HgEmployee savedEmployee = hgEmployeeRepository.save(employee);

        // Then: Verify Add
        assertThat(savedEmployee.getId()).isNotNull();
        Optional<HgEmployee> foundEmployee = hgEmployeeRepository.findById(savedEmployee.getId());
        assertThat(foundEmployee).isPresent();
        assertThat(foundEmployee.get().getFirstName()).isEqualTo("John");

        // When: Remove
        hgEmployeeRepository.delete(savedEmployee);

        // Then: Verify Remove
        Optional<HgEmployee> deletedEmployee = hgEmployeeRepository.findById(savedEmployee.getId());
        assertThat(deletedEmployee).isEmpty();
    }
}
