package pl.tomskr.higienaharmonogrambackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Entity representing a work schedule.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToMany
    @JoinColumn(name = "employee_id")
    private List<HgEmployee> employee;

    @OneToMany
    @JoinColumn(name = "shift_id")
    private List<HgShifts> shift;

}
