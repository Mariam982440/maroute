package com.exemple.maroute.service.impl;

import com.exemple.maroute.dto.VehiculeRequestDTO;
import com.exemple.maroute.dto.VehiculeResponseDTO;
import com.exemple.maroute.entity.Conducteur;
import com.exemple.maroute.entity.Vehicule;
import com.exemple.maroute.exception.BusinessException;
import com.exemple.maroute.exception.ForbiddenException;
import com.exemple.maroute.exception.ResourceNotFoundException;
import com.exemple.maroute.mapper.VehiculeMapper;
import com.exemple.maroute.repository.ConducteurRepository;
import com.exemple.maroute.repository.VehiculeRepository;
import com.exemple.maroute.service.VehiculeService;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class VehiculeServiceImpl implements VehiculeService {

    private static final Logger log = LoggerFactory.getLogger(VehiculeRepository.class);
    private final VehiculeRepository vehiculeRepository;
    private final ConducteurRepository conducteurRepository;
    private final VehiculeMapper vehiculeMapper;

    public VehiculeServiceImpl(
            VehiculeRepository vehiculeRepository,
            ConducteurRepository conducteurRepository,
            VehiculeMapper vehiculeMapper) {
        this.vehiculeRepository = vehiculeRepository;
        this.conducteurRepository = conducteurRepository;
        this.vehiculeMapper = vehiculeMapper;
    }

    @Override
    public VehiculeResponseDTO ajouterVehicule(VehiculeRequestDTO request) {

        Conducteur conducteur = conducteurRepository.findById(request.conducteurId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "conducteur introuvable avec l'id " + request.conducteurId()));

        String immatriculation = request.immatriculation();

        if (vehiculeRepository.existsByImmatriculationIgnoreCase(immatriculation)) {
            log.warn("ajout refusé : immatriculation déjà utilisée ({})", immatriculation);
            throw new BusinessException("cette immatriculation est déjà utilisée");
        }

        Vehicule vehicule = vehiculeMapper.toEntity(request);
        vehicule.setConducteur(conducteur);

        Vehicule vehiculeEnregistre = vehiculeRepository.save(vehicule);
        log.info("véhicule {} ajouté pour le conducteur {}",
                vehiculeEnregistre.getId(), conducteur.getId());

        return vehiculeMapper.toResponseDTO(vehiculeEnregistre);
    }

    @Override
    public VehiculeResponseDTO modifierVehicule (Long id, VehiculeRequestDTO request){

        Vehicule vehicule = vehiculeRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("véhicule introuvable avec l'id "+ id));

        if(!vehicule.getConducteur().getId().equals(request.conducteurId())){
            log.warn("modification refusée : le conducteur {} n'est pas propriétaire du véhicule {}",
                request.conducteurId(), id);
            throw new ForbiddenException("ce véhicule appartient à un autre conducteur");
        }
        String immatriculation = request.immatriculation();

        if(vehiculeRepository.existsByImmatriculationIgnoreCaseAndIdNot(immatriculation,id)){
            log.warn("modification refusée : immatriculation déjà utilisée ({})", immatriculation);
            throw new BusinessException("cette immatriculation est déjà utilisée");
        }
        vehiculeMapper.updateEntity(request,vehicule);

        log.info("véhicule {} modifié", id);
        return vehiculeMapper.toResponseDTO(vehicule);
    }

    @Override
    @Transactional(readOnly = true)
    public VehiculeResponseDTO trouverVehiculeParId(Long id){
        Vehicule vehicule = vehiculeRepository.findById(id)
                .orElseThrow(()->
                        new ResourceNotFoundException("véhicule introuvable avec l'id " + id));

        return vehiculeMapper.toResponseDTO(vehicule);
    }



}
