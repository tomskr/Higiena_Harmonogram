package pl.tomskr.higienaharmonogrambackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.tomskr.higienaharmonogrambackend.entity.HgEmployee;
import pl.tomskr.higienaharmonogrambackend.repository.HgEmployeeRepository;

import java.util.List;

/**
 * Service for managing employees.
 */
@Service
@RequiredArgsConstructor
public class HgEmployeeService {

    private final HgEmployeeRepository hgEmployeeRepository;

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
        employee.setEmployee_Id(employeeDetails.getEmployee_Id());
        return hgEmployeeRepository.save(employee);
    }

    /**
     * Deletes an employee by their ID.
     *
     * @param id the ID of the employee to delete
     */
    public void deleteEmployee(Long id) {
        HgEmployee employee = getEmployeeById(id);
        hgEmployeeRepository.delete(employee);
    }
}
