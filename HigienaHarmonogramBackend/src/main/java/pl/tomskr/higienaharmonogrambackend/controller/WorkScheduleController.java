package pl.tomskr.higienaharmonogrambackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.tomskr.higienaharmonogrambackend.entity.WorkSchedule;
import pl.tomskr.higienaharmonogrambackend.service.WorkScheduleService;

import java.util.List;

@RestController
@RequestMapping("/api/work-schedules")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WorkScheduleController {

    private final WorkScheduleService workScheduleService;

    @GetMapping
    public List<WorkSchedule> getAllWorkSchedules() {
        return workScheduleService.getAllWorkSchedules();
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkSchedule> getWorkScheduleById(@PathVariable Long id) {
        return ResponseEntity.ok(workScheduleService.getWorkScheduleById(id));
    }

    @PostMapping
    public WorkSchedule createWorkSchedule(@RequestBody WorkSchedule workSchedule) {
        return workScheduleService.createWorkSchedule(workSchedule);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkSchedule> updateWorkSchedule(@PathVariable Long id, @RequestBody WorkSchedule workScheduleDetails) {
        return ResponseEntity.ok(workScheduleService.updateWorkSchedule(id, workScheduleDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkSchedule(@PathVariable Long id) {
        workScheduleService.deleteWorkSchedule(id);
        return ResponseEntity.noContent().build();
    }
}
