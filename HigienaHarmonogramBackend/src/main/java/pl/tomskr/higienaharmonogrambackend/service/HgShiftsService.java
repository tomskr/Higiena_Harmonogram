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

/**
 * Service for managing shifts.
 */
@Service
@RequiredArgsConstructor
public class HgShiftsService {

    private final HgShiftsRepository hgShiftsRepository;
    private final HgEmployeeRepository hgEmployeeRepository;

    /**
     * Retrieves all shifts and returns them as a list.
     *
     * @return a list of all shifts
     */
    public List<HgShifts> getAllShifts() {
        return hgShiftsRepository.findAll();
    }

    /**
     * Retrieves a shift by its ID.
     *
     * @param id the ID of the shift to retrieve
     * @return the found shift
     * @throws RuntimeException if the shift is not found
     */
    public HgShifts getShiftById(Long id) {
        return hgShiftsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shift not found with id: " + id));
    }

    /**
     * Retrieves shifts for a specific employee within a given month and year.
     *
     * @param employeeId the ID of the employee
     * @param year the year
     * @param month the month
     * @return a list of shifts matching the criteria
     */
    public List<HgShifts> getShiftsByEmployeeId(Long employeeId,int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());
        return hgShiftsRepository.findByEmployeeIdAndFullDateBetween(employeeId, startDate, endDate);
    }

    /**
     * Creates a new shift.
     *
     * @param shift the shift to create
     * @return the created shift
     * @throws RuntimeException if the employee ID is not provided or the employee is not found
     */
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

    /**
     * Updates an existing shift.
     *
     * @param id the ID of the shift to update
     * @param shiftDetails the new details of the shift
     * @return the updated shift
     */
    public HgShifts updateShift(Long id, HgShifts shiftDetails) {
        HgShifts shift = getShiftById(id);
        shift.setFullDate(shiftDetails.getFullDate());
        shift.setIsHoliday(shiftDetails.getIsHoliday());
        return hgShiftsRepository.save(shift);
    }

    /**
     * Deletes a shift by its ID.
     *
     * @param id the ID of the shift to delete
     */
    public void deleteShift(Long id) {
        HgShifts shift = getShiftById(id);
        hgShiftsRepository.delete(shift);
    }

    /**
     * Fills shifts for a given employee (placeholder implementation).
     *
     * @param id the ID of the employee
     * @return null (as per current implementation)
     */
    public HgShifts fillShift(Long id) {
        HgEmployee employee = hgEmployeeRepository.findById(id).orElse(null);
        if (employee == null || employee.getId() == null) {
            throw new RuntimeException("Employee ID must be provided");
        }

        HgShifts shift = new HgShifts();
        for (int i = 0; i < 3; i++) {
            shift.setEmployee(employee);
            shift.setShiftType('A');
            shift.setShiftLength(8);
            shift.setFullDate(LocalDate.of(2026, 1, i + 1));
            shift.setIsHoliday(false);
            hgShiftsRepository.save(shift);
        }
        return null;
    }
}
