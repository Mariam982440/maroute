package com.exemple.maroute.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record VehiculeRequestDTO(

        @NotBlank(message = "la marque est obligatoire")
        @Size(max = 100, message = "la marque ne doit pas dépasser 100 caractères")
        String marque,

        @NotBlank(message = "le modèle est obligatoire")
        @Size(max = 100, message = "le modèle ne doit pas dépasser 100 caractères")
        String modele,

        @NotBlank(message = "l'immatriculation est obligatoire")
        @Size(max = 30, message = "L'immatriculation ne doit pas dépasser 30 caractères")
        String immatriculation,

        @NotBlank(message = "la couleur est obligatoire")
        @Size(max = 50, message = "la couleur ne doit pas dépasser 50 caractères")
        String couleur,

        @NotNull(message = "la capacité est obligatoire")
        @Min(value = 1, message = "la capacité minimale est de 1 place")
        @Max(value = 8, message = "la capacité maximale est de 8 places")
        Integer capacite,

        @NotNull(message = "le conducteur est obligatoire")
        Long conducteurId

) {}
