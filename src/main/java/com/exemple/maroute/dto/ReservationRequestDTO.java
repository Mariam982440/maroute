package com.exemple.maroute.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReservationRequestDTO(

        @NotNull(message = "le passager est obligatoire")
        Long passagerId,

        @NotNull(message = "le trajet est obligatoire")
        Long trajetId,

        @NotNull(message = "le point de montée est obligatoire")
        Long pointMonteeId,

        @NotNull(message = "le point de descente est obligatoire")
        Long pointDescenteId,

        @NotNull(message = "le nombre de places est obligatoire")
        @Min(value = 1, message = "ll faut réserver au moins une place")
        Integer nombrePlaces

) {}
