package com.exemple.maroute.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record TrajetRequestDTO(

        @NotNull(message = "le conducteur est obligatoire")
        Long conducteurId,

        @NotNull(message = "le véhicule est obligatoire")
        Long vehiculeId,

        @NotNull(message = "le nombre de places proposées est obligatoire")
        @Min(value = 1, message = "au moins une place doit être proposée")
        Integer placesProposees,

        @NotNull(message = "les points d'arrêt sont obligatoires")
        @Size(min = 2, message = "un trajet doit avoir au moins deux points d'arrêt")
        @Valid
        List<PointArretRequestDTO> pointsArret

) {}
