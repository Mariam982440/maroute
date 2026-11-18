package com.exemple.maroute.service;

import com.exemple.maroute.dto.PassagerRequestDTO;
import com.exemple.maroute.dto.PassagerResponseDTO;

import java.util.List;

public interface PassagerService {

    PassagerResponseDTO creerPassager(PassagerRequestDTO request);

    PassagerResponseDTO trouverPassagerParId(Long id);

    List<PassagerResponseDTO> trouverTousLesPassagers();

    PassagerResponseDTO rechercherParEmail(String email);

    PassagerResponseDTO modifierPassager(Long id, PassagerRequestDTO request);
}
