package pl.tomskr.higienaharmonogrambackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.tomskr.higienaharmonogrambackend.entity.WorkSchedule;
import pl.tomskr.higienaharmonogrambackend.repository.WorkScheduleRepository;

import java.util.List;

/**
 * Service for managing work schedules.
 */
@Service
@RequiredArgsConstructor
public class WorkScheduleService {

    private final WorkScheduleRepository workScheduleRepository;

    /**
     * Retrieves all work schedules.
     *
     * @return a list of all work schedules
     */
    public List<WorkSchedule> getAllWorkSchedules() {
        return workScheduleRepository.findAll();
    }

    /**
     * Retrieves a work schedule by its ID.
     *
     * @param id the ID of the work schedule to retrieve
     * @return the found work schedule
     * @throws RuntimeException if the work schedule is not found
     */
    public WorkSchedule getWorkScheduleById(Long id) {
        return workScheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("WorkSchedule not found with id: " + id));
    }

    /**
     * Creates a new work schedule.
     *
     * @param workSchedule the work schedule to create
     * @return the created work schedule
     */
    public WorkSchedule createWorkSchedule(WorkSchedule workSchedule) {
        return workScheduleRepository.save(workSchedule);
    }

    /**
     * Updates an existing work schedule.
     *
     * @param id the ID of the work schedule to update
     * @param workScheduleDetails the new details of the work schedule
     * @return the updated work schedule
     */
    public WorkSchedule updateWorkSchedule(Long id, WorkSchedule workScheduleDetails) {
        WorkSchedule workSchedule = getWorkScheduleById(id);
        workSchedule.setEmployee(workScheduleDetails.getEmployee());
        workSchedule.setShift(workScheduleDetails.getShift());
        return workScheduleRepository.save(workSchedule);
    }

    /**
     * Deletes a work schedule by its ID.
     *
     * @param id the ID of the work schedule to delete
     */
    public void deleteWorkSchedule(Long id) {
        WorkSchedule workSchedule = getWorkScheduleById(id);
        workScheduleRepository.delete(workSchedule);
    }
}
