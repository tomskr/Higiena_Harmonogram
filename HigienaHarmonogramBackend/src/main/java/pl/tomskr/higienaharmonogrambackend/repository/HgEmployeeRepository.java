package pl.tomskr.higienaharmonogrambackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.tomskr.higienaharmonogrambackend.entity.HgEmployee;

@Repository
public interface HgEmployeeRepository extends JpaRepository<HgEmployee, Long> {
}
