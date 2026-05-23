package pl.tomskr.higienaharmonogrambackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.tomskr.higienaharmonogrambackend.entity.HgEmployee;
import pl.tomskr.higienaharmonogrambackend.entity.HgShifts;
import pl.tomskr.higienaharmonogrambackend.repository.HgEmployeeRepository;
import pl.tomskr.higienaharmonogrambackend.repository.HgShiftsRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HgShiftsService {

    private final HgShiftsRepository hgShiftsRepository;
    private final HgEmployeeRepository hgEmployeeRepository;

    public List<HgShifts> getAllShifts() {
        return hgShiftsRepository.findAll();
    }

    public HgShifts getShiftById(Long id) {
        return hgShiftsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shift not found with id: " + id));
    }

    public HgShifts createShift(HgShifts shift) {
        if (shift.getEmployee() == null || shift.getEmployee().getId() == null) {
            throw new RuntimeException("Employee ID must be provided");
        }
        Long employeeId = shift.getEmployee().getId();
        HgEmployee employee = hgEmployeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));
        shift.setEmployee(employee);
        return hgShiftsRepository.save(shift);
    }

    public HgShifts updateShift(Long id, HgShifts shiftDetails) {
        HgShifts shift = getShiftById(id);
        shift.setFullDate(shiftDetails.getFullDate());
        shift.setIsHoliday(shiftDetails.getIsHoliday());
        return hgShiftsRepository.save(shift);
    }

    public void deleteShift(Long id) {
        HgShifts shift = getShiftById(id);
        hgShiftsRepository.delete(shift);
    }
}
