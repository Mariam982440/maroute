package com.exemple.maroute.service;

import com.exemple.maroute.dto.VehiculeRequestDTO;
import com.exemple.maroute.dto.VehiculeResponseDTO;

import java.util.List;

public interface VehiculeService {

    VehiculeResponseDTO ajouterVehicule(VehiculeRequestDTO request);

    VehiculeResponseDTO modifierVehicule(Long id, VehiculeRequestDTO request);

    VehiculeResponseDTO trouverVehiculeParId(Long id);

    List<VehiculeResponseDTO> trouverVehiculesParConducteur(Long conducteurId);
}