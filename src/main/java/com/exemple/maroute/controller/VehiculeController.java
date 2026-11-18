package com.exemple.maroute.controller;


import com.exemple.maroute.dto.VehiculeResponseDTO;
import com.exemple.maroute.service.VehiculeService;
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

    @GetMapping("/conducteurs/{conducteurId}/vehicules")
    public ResponseEntity<List<VehiculeResponseDTO>> listerParConducteur(@PathVariable Long id){
        return ResponseEntity.ok(vehiculeService.trouverVehiculesParConducteur(id));
    }


}
