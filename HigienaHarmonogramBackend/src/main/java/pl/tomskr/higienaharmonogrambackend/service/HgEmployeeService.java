package pl.tomskr.higienaharmonogrambackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.tomskr.higienaharmonogrambackend.entity.HgEmployee;
import pl.tomskr.higienaharmonogrambackend.repository.HgEmployeeRepository;
import pl.tomskr.higienaharmonogrambackend.repository.HgShiftsRepository;

import java.util.List;

/**
 * Service for managing employees.
 */
@Service
@RequiredArgsConstructor
public class HgEmployeeService {

    private final HgEmployeeRepository hgEmployeeRepository;
    private final HgShiftsRepository hgShiftsRepository;

    /**
     * Retrieves all employees.
     *
     * @return a list of all employees
     */
    public List<HgEmployee> getAllEmployees() {
        return hgEmployeeRepository.findAll();
    }

    /**
     * Retrieves an employee by their ID.
     *
     * @param id the ID of the employee to retrieve
     * @return the found employee
     * @throws RuntimeException if the employee is not found
     */
    public HgEmployee getEmployeeById(Long id) {
        return hgEmployeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
    }

    /**
     * Creates a new employee.
     *
     * @param employee the employee to create
     * @return the created employee
     */
    public HgEmployee createEmployee(HgEmployee employee) {
        if (employee.getEmployee_Id() == null || employee.getEmployee_Id().isEmpty()) {
            employee.setEmployee_Id("EMP-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        return hgEmployeeRepository.save(employee);
    }

    /**
     * Updates an existing employee.
     *
     * @param id the ID of the employee to update
     * @param employeeDetails the new details of the employee
     * @return the updated employee
     */
    public HgEmployee updateEmployee(Long id, HgEmployee employeeDetails) {
        HgEmployee employee = getEmployeeById(id);
        employee.setFirstName(employeeDetails.getFirstName());
        employee.setLastName(employeeDetails.getLastName());
        if (employeeDetails.getEmployee_Id() != null && !employeeDetails.getEmployee_Id().isEmpty()) {
            employee.setEmployee_Id(employeeDetails.getEmployee_Id());
        } else if (employee.getEmployee_Id() == null || employee.getEmployee_Id().isEmpty()) {
            // Generate only if both current and new are empty/null
            employee.setEmployee_Id("EMP-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        return hgEmployeeRepository.save(employee);
    }

    /**
     * Deletes an employee by their ID and all associated shifts.
     *
     * @param id the ID of the employee to delete
     */
    @Transactional
    public void deleteEmployee(Long id) {
        HgEmployee employee = getEmployeeById(id);
        hgShiftsRepository.deleteByEmployeeId(id);
        hgEmployeeRepository.delete(employee);
    }
}
