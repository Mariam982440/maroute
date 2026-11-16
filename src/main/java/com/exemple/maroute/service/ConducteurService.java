
package com.exemple.maroute.service;

import com.exemple.maroute.dto.ConducteurRequestDTO;
import com.exemple.maroute.dto.ConducteurResponseDTO;

import java.util.List;

public interface ConducteurService {

    ConducteurResponseDTO creerConducteur(ConducteurRequestDTO request);

    ConducteurResponseDTO trouverConducteurParId(Long id);

    List<ConducteurResponseDTO> trouverTousLesConducteurs();

    ConducteurResponseDTO modifierConducteur(Long id, ConducteurRequestDTO request);

    ConducteurResponseDTO rechercherParEmail(String email);
}
