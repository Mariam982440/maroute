package com.exemple.maroute.service;

import com.exemple.maroute.dto.PageResponseDTO;
import com.exemple.maroute.dto.TrajetRechercheDTO;
import com.exemple.maroute.dto.TrajetRequestDTO;
import com.exemple.maroute.dto.TrajetResponseDTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface TrajetService {

    TrajetResponseDTO publierTrajet(TrajetRequestDTO request);

    TrajetResponseDTO consulterTrajet(Long id);

    void terminerTrajet(Long trajetId, Long conducteurId);

    void annulerTrajet(Long trajetId, Long conducteurId);

    PageResponseDTO<TrajetRechercheDTO> rechercherTrajets(
            String villeDepart, String villeArrivee, LocalDate date,
            Integer nombrePlaces, BigDecimal prixMax, int page, int size);
}
