package pl.tomskr.higienaharmonogrambackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.tomskr.higienaharmonogrambackend.entity.HgEmployee;
import pl.tomskr.higienaharmonogrambackend.entity.HgShifts;
import pl.tomskr.higienaharmonogrambackend.repository.HgEmployeeRepository;
import pl.tomskr.higienaharmonogrambackend.repository.HgShiftsRepository;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HgShiftsService {

    private final HgShiftsRepository hgShiftsRepository;
    private final HgEmployeeRepository hgEmployeeRepository;

    //Get all shifts and put them in a list
    public List<HgShifts> getAllShifts() {
        return hgShiftsRepository.findAll();
    }

    //Get shift by id and throw exception if not found
    public HgShifts getShiftById(Long id) {
        return hgShiftsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shift not found with id: " + id));
    }

    //Get shift by employee id and throw exception if not found
    public List<HgShifts> getShiftsByEmployeeId(Long employeeId,int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());
        return hgShiftsRepository.findByEmployeeIdAndFullDateBetween(employeeId, startDate, endDate);
    }



    //Create shift and throw exception if employee id not found
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

    //Update shift and throw exception if shift id not found
    public HgShifts updateShift(Long id, HgShifts shiftDetails) {
        HgShifts shift = getShiftById(id);
        shift.setFullDate(shiftDetails.getFullDate());
        shift.setIsHoliday(shiftDetails.getIsHoliday());
        return hgShiftsRepository.save(shift);
    }

    //Delete shift and throw exception if shift id not found
    public void deleteShift(Long id) {
        HgShifts shift = getShiftById(id);
        hgShiftsRepository.delete(shift);
    }

    public HgShifts fillShift(HgEmployee employee) {
        //todo:
        return null;
    }
}
