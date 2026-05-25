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

    //Get all employees
    public List<HgEmployee> getAllEmployees() {
        return hgEmployeeRepository.findAll();
    }

    //Get employee by id and throw exception if employee id not found
    public HgEmployee getEmployeeById(Long id) {
        return hgEmployeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
    }

    //Create employee and throw exception if employee id already exists
    public HgEmployee createEmployee(HgEmployee employee) {
        return hgEmployeeRepository.save(employee);
    }

    //Update employee and throw exception if employee id not found
    public HgEmployee updateEmployee(Long id, HgEmployee employeeDetails) {
        HgEmployee employee = getEmployeeById(id);
        employee.setFirstName(employeeDetails.getFirstName());
        employee.setLastName(employeeDetails.getLastName());
        employee.setEmployee_Id(employeeDetails.getEmployee_Id());
        return hgEmployeeRepository.save(employee);
    }

    //Delete employee and throw exception if employee id not found
    public void deleteEmployee(Long id) {
        HgEmployee employee = getEmployeeById(id);
        hgEmployeeRepository.delete(employee);
    }
}
