package pl.tomskr.higienaharmonogrambackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.tomskr.higienaharmonogrambackend.entity.WorkSchedule;
import pl.tomskr.higienaharmonogrambackend.repository.WorkScheduleRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkScheduleService {

    private final WorkScheduleRepository workScheduleRepository;

    public List<WorkSchedule> getAllWorkSchedules() {
        return workScheduleRepository.findAll();
    }

    //Get work schedule by id and throw exception if work schedule id not found
    public WorkSchedule getWorkScheduleById(Long id) {
        return workScheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("WorkSchedule not found with id: " + id));
    }

    //Create work schedule and throw exception if work schedule id already exists
    public WorkSchedule createWorkSchedule(WorkSchedule workSchedule) {
        return workScheduleRepository.save(workSchedule);
    }

    //Update work schedule and throw exception if work schedule id not found
    public WorkSchedule updateWorkSchedule(Long id, WorkSchedule workScheduleDetails) {
        WorkSchedule workSchedule = getWorkScheduleById(id);
        workSchedule.setEmployee(workScheduleDetails.getEmployee());
        workSchedule.setShift(workScheduleDetails.getShift());
        return workScheduleRepository.save(workSchedule);
    }

    //Delete work schedule and throw exception if work schedule id not found
    public void deleteWorkSchedule(Long id) {
        WorkSchedule workSchedule = getWorkScheduleById(id);
        workScheduleRepository.delete(workSchedule);
    }
}
