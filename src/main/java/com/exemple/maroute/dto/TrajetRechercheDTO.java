package com.exemple.maroute.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public record TrajetRechercheDTO(
        Long trajetId,
        Long conducteurId,
        String conducteurPrenom,
        String conducteurNom,
        Long pointMonteeId,
        String villeMontee,
        LocalDateTime heureMontee,
        Long pointDescenteId,
        String villeDescente,
        LocalDateTime heureDescente,
        BigDecimal prixSegment,
        long placesRestantes,
        LocalDateTime dateDepartTrajet
) {}
