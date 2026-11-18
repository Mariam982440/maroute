package com.exemple.maroute.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PointArretRequestDTO(

        @NotBlank(message = "la ville est obligatoire")
        @Size(max = 100, message = "la ville ne doit pas dépasser 100 caractères")
        String ville,

        @NotNull(message = "l'heure de passage est obligatoire")
        LocalDateTime heurePassage,

        @NotNull(message = "le prix depuis le départ est obligatoire")
        @DecimalMin(value = "0.00", message = "le prix ne peut pas être négatif")
        @Digits(integer = 8, fraction = 2, message = "le prix doit avoir au plus 2 décimales")
        BigDecimal prixDepuisDepart

) {}
