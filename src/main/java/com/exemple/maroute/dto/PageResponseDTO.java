package com.exemple.maroute.dto;

import org.springframework.data.domain.Page;

import java.util.List;


public record PageResponseDTO<T>(
        List<T> contenu,
        int page,
        int taille,
        long totalElements,
        int totalPages
) {

    public static <T> PageResponseDTO<T> of(Page<T> page) {
        return new PageResponseDTO<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }
}
