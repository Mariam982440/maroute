package com.exemple.maroute.dto;

import com.exemple.maroute.enums.StatutTrajet;

import java.time.LocalDateTime;
import java.util.List;

public record TrajetResponseDTO(
        Long id,
        ConducteurResponseDTO conducteur,
        VehiculeResponseDTO vehicule,
        String villeDepart,
        String villeArrivee,
        LocalDateTime dateDepart,
        int placesProposees,
        int placesRestantes,
        StatutTrajet statut,
        List<PointArretResponseDTO> pointsArret
) {}
