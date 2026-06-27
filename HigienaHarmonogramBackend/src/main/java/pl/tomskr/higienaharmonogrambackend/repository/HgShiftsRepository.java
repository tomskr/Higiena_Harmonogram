package pl.tomskr.higienaharmonogrambackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.tomskr.higienaharmonogrambackend.entity.HgShifts;

import java.time.LocalDate;
import java.util.List;

//Repository for HgShifts entity
@Repository
public interface HgShiftsRepository extends JpaRepository<HgShifts, Long> {
    List<HgShifts> findByEmployeeIdAndFullDateBetween(Long employeeId, LocalDate startDate, LocalDate endDate);
    boolean existsByEmployeeIdAndFullDate(Long employeeId, LocalDate fullDate);

    @Transactional
    void deleteByEmployeeId(Long employeeId);
}
