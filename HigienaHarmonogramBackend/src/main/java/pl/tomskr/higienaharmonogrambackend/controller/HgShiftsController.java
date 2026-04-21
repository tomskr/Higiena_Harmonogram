package pl.tomskr.higienaharmonogrambackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.tomskr.higienaharmonogrambackend.entity.HgShifts;
import pl.tomskr.higienaharmonogrambackend.service.HgShiftsService;

import java.util.List;

@RestController
@RequestMapping("/api/shifts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HgShiftsController {

    private final HgShiftsService hgShiftsService;

    @GetMapping
    public List<HgShifts> getAllShifts() {
        return hgShiftsService.getAllShifts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<HgShifts> getShiftById(@PathVariable Long id) {
        return ResponseEntity.ok(hgShiftsService.getShiftById(id));
    }

    @PostMapping
    public HgShifts createShift(@RequestBody HgShifts shift) {
        return hgShiftsService.createShift(shift);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HgShifts> updateShift(@PathVariable Long id, @RequestBody HgShifts shiftDetails) {
        return ResponseEntity.ok(hgShiftsService.updateShift(id, shiftDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShift(@PathVariable Long id) {
        hgShiftsService.deleteShift(id);
        return ResponseEntity.noContent().build();
    }
}
