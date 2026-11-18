package com.exemple.maroute.dto;

public record PassagerResponseDTO(
        Long id,
        String nom,
        String prenom,
        String email,
        String telephone
) {}
