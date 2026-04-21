package pl.tomskr.higienaharmonogrambackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.tomskr.higienaharmonogrambackend.entity.HgEmployee;
import pl.tomskr.higienaharmonogrambackend.repository.HgEmployeeRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HgEmployeeService {

    private final HgEmployeeRepository hgEmployeeRepository;

    public List<HgEmployee> getAllEmployees() {
        return hgEmployeeRepository.findAll();
    }

    public HgEmployee getEmployeeById(Long id) {
        return hgEmployeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
    }

    public HgEmployee createEmployee(HgEmployee employee) {
        return hgEmployeeRepository.save(employee);
    }

    public HgEmployee updateEmployee(Long id, HgEmployee employeeDetails) {
        HgEmployee employee = getEmployeeById(id);
        employee.setFirstName(employeeDetails.getFirstName());
        employee.setLastName(employeeDetails.getLastName());
        employee.setEmployeeId(employeeDetails.getEmployeeId());
        return hgEmployeeRepository.save(employee);
    }

    public void deleteEmployee(Long id) {
        HgEmployee employee = getEmployeeById(id);
        hgEmployeeRepository.delete(employee);
    }
}
