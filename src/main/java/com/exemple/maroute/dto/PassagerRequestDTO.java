package com.exemple.maroute.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PassagerRequestDTO(

        @NotBlank(message = "Le nom est obligatoire")
        @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
        String nom,

        @NotBlank(message = "Le prénom est obligatoire")
        @Size(max = 100, message = "Le prénom ne doit pas dépasser 100 caractères")
        String prenom,

        @NotBlank(message = "L'email est obligatoire")
        @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
                message = "L'adresse email doit être valide")
        @Size(max = 150, message = "L'email ne doit pas dépasser 150 caractères")
        String email,

        @NotBlank(message = "Le téléphone est obligatoire")
        @Size(max = 20, message = "Le téléphone ne doit pas dépasser 20 caractères")
        String telephone

) {}
