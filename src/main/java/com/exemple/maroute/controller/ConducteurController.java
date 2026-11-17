package com.exemple.maroute.controller;

import com.exemple.maroute.dto.ConducteurRequestDTO;
import com.exemple.maroute.dto.ConducteurResponseDTO;
import com.exemple.maroute.entity.Conducteur;
import com.exemple.maroute.service.ConducteurService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conducteurs")
public class ConducteurController {

    private final ConducteurService conducteurService;

    public ConducteurController(ConducteurService conducteurService) {
        this.conducteurService = conducteurService;
    }

    @PostMapping
    public ResponseEntity<ConducteurResponseDTO> creer(
            @Valid @RequestBody ConducteurRequestDTO request) {
        ConducteurResponseDTO cree = conducteurService.creerConducteur(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(cree);
    }

    @GetMapping
    public ResponseEntity<List<ConducteurResponseDTO>> lister(){
        return ResponseEntity.ok(conducteurService.trouverTousLesConducteurs());
    }



}
