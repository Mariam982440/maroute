package com.exemple.maroute.service.impl;


import com.exemple.maroute.dto.PointArretRequestDTO;
import com.exemple.maroute.dto.TrajetRequestDTO;
import com.exemple.maroute.dto.TrajetResponseDTO;
import com.exemple.maroute.entity.Conducteur;
import com.exemple.maroute.entity.Vehicule;
import com.exemple.maroute.exception.ForbiddenException;
import com.exemple.maroute.exception.ResourceNotFoundException;
import com.exemple.maroute.mapper.TrajetMapper;
import com.exemple.maroute.repository.ConducteurRepository;
import com.exemple.maroute.repository.TrajetRepository;
import com.exemple.maroute.repository.VehiculeRepository;
import com.exemple.maroute.service.TrajetService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
@Transactional
public class TrajetServiceImpl implements TrajetService {

    private static final Logger log = LoggerFactory.getLogger(TrajetServiceImpl.class);

    private final TrajetRepository trajetRepository;
    private final ConducteurRepository conducteurRepository;
    private final VehiculeRepository vehiculeRepository;
    private final TrajetMapper trajetMapper;

    public TrajetServiceImpl(
            TrajetRepository trajetRepository,
            ConducteurRepository conducteurRepository,
            VehiculeRepository vehiculeRepository,
            TrajetMapper trajetMapper) {
        this.trajetRepository = trajetRepository;
        this.conducteurRepository = conducteurRepository;
        this.vehiculeRepository = vehiculeRepository;
        this.trajetMapper = trajetMapper;
    }

    @Override
    public TrajetResponseDTO publierTrajet(TrajetRequestDTO request) {

        Conducteur conducteur = conducteurRepository.findById(request.conducteurId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "conducteur introuvable avec l'id " + request.conducteurId()));

        Vehicule vehicule = vehiculeRepository.findById(request.vehiculeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "véhicule introuvable avec l'id " + request.vehiculeId()));

        if (!vehicule.getConducteur().getId().equals(conducteur.getId())) {
            log.warn("publication refusée : le véhicule {} n'appartient pas au conducteur {}",
                    vehicule.getId(), conducteur.getId());
            throw new ForbiddenException("ce véhicule appartient à un autre conducteur");
        }

        if (request.placesProposees() > vehicule.getCapacite()){

        }

        List<PointArretRequestDTO> points = request.pointsArret();

    }

}