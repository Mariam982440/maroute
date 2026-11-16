package com.exemple.maroute.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ConducteurRequestDTO(

        @NotBlank(message = "le nom est obligatoire")
        @Size(max = 100, message = "le nom ne doit pas dépasser 100 caractères")
        String nom,

        @NotBlank(message = "le prénom est obligatoire")
        @Size(max = 100, message = "le prénom ne doit pas dépasser 100 caractères")
        String prenom,

        @NotBlank(message = "l'email est obligatoire")
        @Email(message = "l'adresse email doit être valide")
        @Size(max = 150, message = "l'email ne doit pas dépasser 150 caractères")
        String email,

        @NotBlank(message = "le téléphone est obligatoire")
        @Size(max = 20, message = "Le téléphone ne doit pas dépasser 20 caractères")
        String telephone,

        @NotBlank(message = "le numéro de permis est obligatoire")
        @Size(max = 50, message = "le numéro de permis ne doit pas dépasser 50 caractères")
        String numeroPermis

) {}
