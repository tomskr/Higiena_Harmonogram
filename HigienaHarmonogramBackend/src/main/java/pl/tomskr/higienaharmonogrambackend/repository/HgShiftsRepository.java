package pl.tomskr.higienaharmonogrambackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.tomskr.higienaharmonogrambackend.entity.HgShifts;

@Repository
public interface HgShiftsRepository extends JpaRepository<HgShifts, Long> {
}
