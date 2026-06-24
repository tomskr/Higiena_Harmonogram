package pl.tomskr.higienaharmonogrambackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.tomskr.higienaharmonogrambackend.entity.HgEmployee;
import pl.tomskr.higienaharmonogrambackend.service.HgEmployeeService;

import java.util.List;

/**
 * REST controller for managing employees.
 */
@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HgEmployeeController {

    private final HgEmployeeService hgEmployeeService;

    /**
     * Retrieves all employees.
     *
     * @return a list of all employees
     */
    @GetMapping
    public List<HgEmployee> getAllEmployees() {
        return hgEmployeeService.getAllEmployees();
    }

    /**
     * Retrieves an employee by their ID.
     *
     * @param id the ID of the employee to retrieve
     * @return the employee with the specified ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<HgEmployee> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(hgEmployeeService.getEmployeeById(id));
    }

    /**
     * Creates a new employee.
     *
     * @param employee the employee to create
     * @return the created employee
     */
    @PostMapping
    public HgEmployee createEmployee(@RequestBody HgEmployee employee) {
        return hgEmployeeService.createEmployee(employee);
    }

    /**
     * Updates an existing employee.
     *
     * @param id the ID of the employee to update
     * @param employeeDetails the new details of the employee
     * @return the updated employee
     */
    @PutMapping("/{id}")
    public ResponseEntity<HgEmployee> updateEmployee(@PathVariable Long id, @RequestBody HgEmployee employeeDetails) {
        return ResponseEntity.ok(hgEmployeeService.updateEmployee(id, employeeDetails));
    }

    /**
     * Deletes an employee by their ID.
     *
     * @param id the ID of the employee to delete
     * @return an empty response with status 204 (No Content)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        hgEmployeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
