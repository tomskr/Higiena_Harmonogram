package pl.tomskr.higienaharmonogrambackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.tomskr.higienaharmonogrambackend.entity.HgEmployee;
import pl.tomskr.higienaharmonogrambackend.service.HgEmployeeService;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HgEmployeeController {

    private final HgEmployeeService hgEmployeeService;

    @GetMapping
    public List<HgEmployee> getAllEmployees() {
        return hgEmployeeService.getAllEmployees();
    }

    @GetMapping("/{id}")
    public ResponseEntity<HgEmployee> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(hgEmployeeService.getEmployeeById(id));
    }

    @PostMapping
    public HgEmployee createEmployee(@RequestBody HgEmployee employee) {
        return hgEmployeeService.createEmployee(employee);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HgEmployee> updateEmployee(@PathVariable Long id, @RequestBody HgEmployee employeeDetails) {
        return ResponseEntity.ok(hgEmployeeService.updateEmployee(id, employeeDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        hgEmployeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
