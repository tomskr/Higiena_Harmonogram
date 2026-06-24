package pl.tomskr.higienaharmonogrambackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.tomskr.higienaharmonogrambackend.entity.WorkSchedule;
import pl.tomskr.higienaharmonogrambackend.service.WorkScheduleService;

import java.util.List;

/**
 * REST controller for managing work schedules.
 */
@RestController
@RequestMapping("/api/work-schedules")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WorkScheduleController {

    private final WorkScheduleService workScheduleService;

    /**
     * Retrieves all work schedules.
     *
     * @return a list of all work schedules
     */
    @GetMapping
    public List<WorkSchedule> getAllWorkSchedules() {
        return workScheduleService.getAllWorkSchedules();
    }

    /**
     * Retrieves a work schedule by its ID.
     *
     * @param id the ID of the work schedule to retrieve
     * @return the work schedule with the specified ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<WorkSchedule> getWorkScheduleById(@PathVariable Long id) {
        return ResponseEntity.ok(workScheduleService.getWorkScheduleById(id));
    }

    /**
     * Creates a new work schedule.
     *
     * @param workSchedule the work schedule to create
     * @return the created work schedule
     */
    @PostMapping
    public WorkSchedule createWorkSchedule(@RequestBody WorkSchedule workSchedule) {
        return workScheduleService.createWorkSchedule(workSchedule);
    }

    /**
     * Updates an existing work schedule.
     *
     * @param id the ID of the work schedule to update
     * @param workScheduleDetails the new details of the work schedule
     * @return the updated work schedule
     */
    @PutMapping("/{id}")
    public ResponseEntity<WorkSchedule> updateWorkSchedule(@PathVariable Long id, @RequestBody WorkSchedule workScheduleDetails) {
        return ResponseEntity.ok(workScheduleService.updateWorkSchedule(id, workScheduleDetails));
    }

    /**
     * Deletes a work schedule by its ID.
     *
     * @param id the ID of the work schedule to delete
     * @return an empty response with status 204 (No Content)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkSchedule(@PathVariable Long id) {
        workScheduleService.deleteWorkSchedule(id);
        return ResponseEntity.noContent().build();
    }
}
