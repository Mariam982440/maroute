package com.exemple.maroute.dto;

import com.exemple.maroute.enums.StatutReservation;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReservationResponseDTO(
        Long id,
        Long passagerId,
        Long trajetId,
        Long pointMonteeId,
        String villeMontee,
        Long pointDescenteId,
        String villeDescente,
        int nombrePlaces,
        BigDecimal montantTotal,
        LocalDateTime dateReservation,
        StatutReservation statut
) {}
