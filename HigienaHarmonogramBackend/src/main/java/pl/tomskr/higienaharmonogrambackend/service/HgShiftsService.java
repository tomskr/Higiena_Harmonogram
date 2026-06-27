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

import java.util.Optional;

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
    public List<HgShifts>  getShiftsByEmployeeId(Long employeeId,int year, int month) {
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
        LocalDate shiftDate = shift.getFullDate();

        if (shiftDate == null) {
            throw new RuntimeException("Shift date must be provided");
        }

        if (hgShiftsRepository.existsByEmployeeIdAndFullDate(employeeId, shiftDate)) {
            throw new RuntimeException("Employee already has a shift on this day");
        }

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

        if (shiftDetails.getFullDate() != null && !shiftDetails.getFullDate().equals(shift.getFullDate())) {
            if (hgShiftsRepository.existsByEmployeeIdAndFullDate(shift.getEmployee().getId(), shiftDetails.getFullDate())) {
                throw new RuntimeException("Employee already has a shift on this day");
            }
        }

        shift.setFullDate(shiftDetails.getFullDate());
        shift.setIsHoliday(shiftDetails.getIsHoliday());
        shift.setShiftType(shiftDetails.getShiftType());
        shift.setShiftLength(shiftDetails.getShiftLength());
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
     * Fills shifts for all employees between the earliest and latest months found in existing shifts.
     */
    public void fillShifts() {
        Optional<HgShifts> earliestShift = hgShiftsRepository.findFirstByOrderByFullDateAsc();
        Optional<HgShifts> latestShift = hgShiftsRepository.findFirstByOrderByFullDateDesc();

        if (earliestShift.isEmpty() || latestShift.isEmpty()) {
            return;
        }

        LocalDate startDate = earliestShift.get().getFullDate().with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endDate = latestShift.get().getFullDate().with(TemporalAdjusters.lastDayOfMonth());

        List<HgEmployee> employees = hgEmployeeRepository.findAll();

        for (HgEmployee employee : employees) {
            fillEmployeeShifts(employee, startDate, endDate);
        }
    }

    /**
     * Fills shifts for a given employee between startDate and endDate.
     */
    private void fillEmployeeShifts(HgEmployee employee, LocalDate startDate, LocalDate endDate) {
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            if (!hgShiftsRepository.existsByEmployeeIdAndFullDate(employee.getId(), currentDate)) {
                HgShifts newShift = HgShifts.builder()
                        .employee(employee)
                        .shiftType('A')
                        .shiftLength(8)
                        .fullDate(currentDate)
                        .isHoliday(false)
                        .build();
                hgShiftsRepository.save(newShift);
            }
            currentDate = currentDate.plusDays(1);
        }
    }

}
