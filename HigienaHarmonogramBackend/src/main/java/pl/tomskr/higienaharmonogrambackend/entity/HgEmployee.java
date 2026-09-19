package pl.tomskr.higienaharmonogrambackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing an employee.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HgEmployee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String photo;

    @Column(nullable = false, unique = true)
    private String employee_Id; // Unique identifier if needed

}
