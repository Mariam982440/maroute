package com.exemple.maroute.service.impl;


import com.exemple.maroute.dto.PointArretRequestDTO;
import com.exemple.maroute.dto.TrajetRequestDTO;
import com.exemple.maroute.dto.TrajetResponseDTO;
import com.exemple.maroute.entity.Conducteur;
import com.exemple.maroute.entity.Vehicule;
import com.exemple.maroute.exception.ForbiddenException;
import com.exemple.maroute.exception.InvalidRequestException;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    private void verifierParcours(List<PointArretRequestDTO> points){

        if (!points.get(0).heurePassage().isAfter(LocalDateTime.now())){
            throw  refus("l'heure de départ doit être dans le futur");
        }

        if (points.get(0).prixDepuisDepart().compareTo(BigDecimal.ZERO) != 0){
            throw refus("le prix du point de départ doit être égal à 0");
        }

        Set<String> villes = new HashSet<>();
        for (int i = 0; i < points.size(); i++) {
            PointArretRequestDTO point = points.get(i);

            if (!villes.add(point.ville().trim().toLowerCase())) {
                throw refus("la ville " + point.ville().trim() + " apparaît plusieurs fois sur le parcours");
            }

            if (i > 0) {
                PointArretRequestDTO precedent = points.get(i - 1);
                if (!point.heurePassage().isAfter(precedent.heurePassage())) {
                    throw refus("les heures de passage doivent être strictement croissantes");
                }
                if (point.prixDepuisDepart().compareTo(precedent.prixDepuisDepart()) <= 0) {
                    throw refus("les prix depuis le départ doivent être strictement croissants");
                }
            }
        }

    }

    private InvalidRequestException refus(String message){
        log.warn("publication refusée: {}",message);
        throw new InvalidRequestException(message);
    }

}