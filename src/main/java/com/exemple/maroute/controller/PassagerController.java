package com.exemple.maroute.controller;


import com.exemple.maroute.dto.PassagerRequestDTO;
import com.exemple.maroute.dto.PassagerResponseDTO;
import com.exemple.maroute.service.PassagerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/passagers")
public class PassagerController {

    private final PassagerService passagerService;

    public PassagerController(PassagerService passagerService) {
        this.passagerService = passagerService;
    }

    @PostMapping
    public ResponseEntity<PassagerResponseDTO> creer(
            @Valid @RequestBody PassagerRequestDTO request) {
        PassagerResponseDTO cree = passagerService.creerPassager(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(cree);
    }

    @GetMapping
    public ResponseEntity<List<PassagerResponseDTO>> lister() {
        return ResponseEntity.ok(passagerService.trouverTousLesPassagers());
    }
    @GetMapping("/{id}")
    public ResponseEntity<PassagerResponseDTO> consulter(@PathVariable Long id) {
        return ResponseEntity.ok(passagerService.trouverPassagerParId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PassagerResponseDTO> modifier(
            @PathVariable Long id,
            @Valid @RequestBody PassagerRequestDTO request) {
        return ResponseEntity.ok(passagerService.modifierPassager(id, request));
    }


}