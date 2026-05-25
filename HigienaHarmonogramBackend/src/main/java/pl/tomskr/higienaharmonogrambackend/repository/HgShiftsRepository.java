package pl.tomskr.higienaharmonogrambackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.tomskr.higienaharmonogrambackend.entity.HgShifts;

import java.util.List;

//Repository for HgShifts entity
@Repository
public interface HgShiftsRepository extends JpaRepository<HgShifts, Long> {
    List<HgShifts> findByEmployeeId(Long employeeId);
}
