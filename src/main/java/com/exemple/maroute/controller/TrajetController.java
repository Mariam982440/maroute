package com.exemple.maroute.controller;


import com.exemple.maroute.dto.TrajetRequestDTO;
import com.exemple.maroute.dto.TrajetResponseDTO;
import com.exemple.maroute.entity.Trajet;
import com.exemple.maroute.service.TrajetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/trajets")
public class TrajetController {

    private final TrajetService trajetService ;

    public TrajetController(TrajetService trajetService){
        this.trajetService = trajetService;
    }

    @PostMapping
    public ResponseEntity<TrajetResponseDTO> publier(@Valid @RequestBody TrajetRequestDTO trajetRequestDTO){
        TrajetResponseDTO publie = trajetService.publierTrajet(trajetRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(publie);
    }

}
