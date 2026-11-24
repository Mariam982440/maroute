package com.exemple.maroute.controller;

import com.exemple.maroute.dto.PageResponseDTO;
import com.exemple.maroute.dto.TrajetRechercheDTO;
import com.exemple.maroute.dto.TrajetRequestDTO;
import com.exemple.maroute.dto.TrajetResponseDTO;
import com.exemple.maroute.service.TrajetService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
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

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/trajets")
public class TrajetController {

    private final TrajetService trajetService;

    public TrajetController(TrajetService trajetService) {
        this.trajetService = trajetService;
    }

    @PostMapping
    public ResponseEntity<TrajetResponseDTO> publier(
            @Valid @RequestBody TrajetRequestDTO request) {
        TrajetResponseDTO publie = trajetService.publierTrajet(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(publie);
    }

    // GET /api/trajets/recherche?villeDepart=Settat&villeArrivee=Marrakech&date=2026-11-27
    //     &nombrePlaces=1&prixMax=60&page=0&size=10
    @GetMapping("/recherche")
    public ResponseEntity<PageResponseDTO<TrajetRechercheDTO>> rechercher(
            @RequestParam String villeDepart,
            @RequestParam String villeArrivee,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Integer nombrePlaces,
            @RequestParam(required = false) BigDecimal prixMax,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(trajetService.rechercherTrajets(
                villeDepart, villeArrivee, date, nombrePlaces, prixMax, page, size));
    }

    // PATCH /api/trajets/{id}/terminer?conducteurId=1 -> 204
    @PatchMapping("/{id}/terminer")
    public ResponseEntity<Void> terminer(
            @PathVariable Long id,
            @RequestParam Long conducteurId) {
        trajetService.terminerTrajet(id, conducteurId);
        return ResponseEntity.noContent().build();
    }

    // PATCH /api/trajets/{id}/annuler?conducteurId=1 -> 204
    @PatchMapping("/{id}/annuler")
    public ResponseEntity<Void> annuler(
            @PathVariable Long id,
            @RequestParam Long conducteurId) {
        trajetService.annulerTrajet(id, conducteurId);
        return ResponseEntity.noContent().build();
    }

    // GET /api/trajets/{id} -> détail (parcours, places restantes, conducteur)
    @GetMapping("/{id}")
    public ResponseEntity<TrajetResponseDTO> consulter(@PathVariable Long id) {
        return ResponseEntity.ok(trajetService.consulterTrajet(id));
    }
}
