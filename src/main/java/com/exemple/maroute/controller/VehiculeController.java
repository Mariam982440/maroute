package com.exemple.maroute.controller;


import com.exemple.maroute.dto.VehiculeRequestDTO;
import com.exemple.maroute.dto.VehiculeResponseDTO;
import com.exemple.maroute.entity.Vehicule;
import com.exemple.maroute.service.VehiculeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class VehiculeController {

    public final VehiculeService vehiculeService;

    public VehiculeController(VehiculeService vehiculeService) {
        this.vehiculeService = vehiculeService;
    }


    @PostMapping("vehicules")
    public ResponseEntity<VehiculeResponseDTO> ajouter(
            @Valid @RequestBody VehiculeRequestDTO request){
        VehiculeResponseDTO cree = vehiculeService.ajouterVehicule(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(cree);
    }


    @PutMapping("/vehicules/{id}")
    public ResponseEntity<VehiculeResponseDTO> modifier(
            @PathVariable Long id,
            @Valid @RequestBody VehiculeRequestDTO request) {
        return ResponseEntity.ok(vehiculeService.modifierVehicule(id, request));
    }

    @GetMapping("/vehicules/{id}")
    public ResponseEntity<VehiculeResponseDTO> consulter(
            @PathVariable Long id) {
        return ResponseEntity.ok(vehiculeService.trouverVehiculeParId(id));
    }

    @GetMapping("/conducteurs/{conducteurId}/vehicules")
    public ResponseEntity<List<VehiculeResponseDTO>> listerParConducteur(
            @PathVariable Long conducteurId) {
        return ResponseEntity.ok(vehiculeService.trouverVehiculesParConducteur(conducteurId));
    }


}
