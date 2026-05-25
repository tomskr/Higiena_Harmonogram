package pl.tomskr.higienaharmonogrambackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.tomskr.higienaharmonogrambackend.entity.WorkSchedule;

//Repository for WorkSchedule entity
@Repository
public interface WorkScheduleRepository extends JpaRepository<WorkSchedule, Long> {
}
