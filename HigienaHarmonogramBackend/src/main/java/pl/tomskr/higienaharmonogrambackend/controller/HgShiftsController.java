package pl.tomskr.higienaharmonogrambackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.tomskr.higienaharmonogrambackend.entity.HgEmployee;
import pl.tomskr.higienaharmonogrambackend.entity.HgShifts;
import pl.tomskr.higienaharmonogrambackend.service.HgShiftsService;

import java.util.List;

/**
 * REST controller for managing shifts.
 */
@RestController
@RequestMapping("/api/shifts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HgShiftsController {

    private final HgShiftsService hgShiftsService;

    /**
     * Retrieves all shifts.
     *
     * @return a list of all shifts
     */
    @GetMapping
    public List<HgShifts> getAllShifts() {
        return hgShiftsService.getAllShifts();
    }

    /**
     * Retrieves a shift by its ID.
     *
     * @param id the ID of the shift to retrieve
     * @return the shift with the specified ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<HgShifts> getShiftById(@PathVariable Long id) {
        return ResponseEntity.ok(hgShiftsService.getShiftById(id));
    }

    /**
     * Creates a new shift.
     *
     * @param shift the shift to create
     * @return the created shift
     */
    @PostMapping
    public HgShifts createShift(@RequestBody HgShifts shift) {
        return hgShiftsService.createShift(shift);
    }

    /**
     * Updates an existing shift.
     *
     * @param id the ID of the shift to update
     * @param shiftDetails the new details of the shift
     * @return the updated shift
     */
    @PutMapping("/{id}")
    public ResponseEntity<HgShifts> updateShift(@PathVariable Long id, @RequestBody HgShifts shiftDetails) {
        return ResponseEntity.ok(hgShiftsService.updateShift(id, shiftDetails));
    }


    /**
     * Fills shifts for all employees between the earliest and latest months found.
     *
     * @return a 200 OK response
     */
    @PostMapping("/fill")
    public ResponseEntity<Void> fillShifts() {
        hgShiftsService.fillShifts();
        return ResponseEntity.ok().build();
    }

    /**
     * Deletes a shift by its ID.
     *
     * @param id the ID of the shift to delete
     * @return an empty response with status 204 (No Content)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShift(@PathVariable Long id) {
        hgShiftsService.deleteShift(id);
        return ResponseEntity.noContent().build();
    }
}
