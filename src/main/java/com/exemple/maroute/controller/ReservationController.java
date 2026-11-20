package com.exemple.maroute.controller;

import com.exemple.maroute.dto.ReservationRequestDTO;
import com.exemple.maroute.dto.ReservationResponseDTO;
import com.exemple.maroute.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponseDTO> reserver(
            @Valid @RequestBody ReservationRequestDTO request) {
        ReservationResponseDTO cree = reservationService.reserver(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(cree);
    }


    @PatchMapping("/reservations/{id}/annuler")
    public ResponseEntity<Void> annuler(
            @PathVariable Long id,
            @RequestParam Long passagerId) {
        reservationService.annulerReservation(id, passagerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/passagers/{passagerId}/reservations")
    public ResponseEntity<List<ReservationResponseDTO>> historiquePassager(
            @PathVariable Long passagerId) {
        return ResponseEntity.ok(reservationService.consulterReservationsPassager(passagerId));
    }

    @GetMapping("/trajets/{trajetId}/reservations")
    public ResponseEntity<List<ReservationResponseDTO>> reservationsDuTrajet(
            @PathVariable Long trajetId,
            @RequestParam Long conducteurId) {
        return ResponseEntity.ok(
                reservationService.consulterReservationsTrajet(trajetId, conducteurId));
    }
}
