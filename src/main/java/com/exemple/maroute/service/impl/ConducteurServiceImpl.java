package com.exemple.maroute.service.impl;

import com.exemple.maroute.dto.ConducteurRequestDTO;
import com.exemple.maroute.dto.ConducteurResponseDTO;
import com.exemple.maroute.entity.Conducteur;
import com.exemple.maroute.exception.BusinessException;
import com.exemple.maroute.exception.ResourceNotFoundException;
import com.exemple.maroute.mapper.ConducteurMapper;
import com.exemple.maroute.repository.ConducteurRepository;
import com.exemple.maroute.service.ConducteurService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ConducteurServiceImpl implements ConducteurService {

    private static final Logger log = LoggerFactory.getLogger(ConducteurServiceImpl.class);

    private final ConducteurRepository conducteurRepository;
    private final ConducteurMapper conducteurMapper;

    public ConducteurServiceImpl(
            ConducteurRepository conducteurRepository,
            ConducteurMapper conducteurMapper) {
        this.conducteurRepository = conducteurRepository;
        this.conducteurMapper = conducteurMapper;
    }

    @Override
    public ConducteurResponseDTO creerConducteur(ConducteurRequestDTO request) {

        String email = normaliserEmail(request.email());

        if (conducteurRepository.existsByEmailIgnoreCase(email)) {
            log.warn("création refusée : email déjà utilisé ({})", email);
            throw new BusinessException("cet email est déjà utilisé");
        }
        if (conducteurRepository.existsByNumeroPermis(request.numeroPermis())) {
            log.warn("création refusée : permis déjà utilisé ({})", request.numeroPermis());
            throw new BusinessException("ce numéro de permis est déjà utilisé");
        }

        Conducteur conducteur = conducteurMapper.toEntity(request);
        conducteur.setEmail(email);

        Conducteur conducteurEnregistre = conducteurRepository.save(conducteur);
        log.info("conducteur créé avec l'id {}", conducteurEnregistre.getId());

        return conducteurMapper.toResponseDTO(conducteurEnregistre);
    }

    @Override
    @Transactional(readOnly = true)
    public ConducteurResponseDTO trouverConducteurParId(Long id) {

        Conducteur conducteur = conducteurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "conducteur introuvable avec l'id " + id));

        return conducteurMapper.toResponseDTO(conducteur);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConducteurResponseDTO> trouverTousLesConducteurs() {

        return conducteurRepository.findAll()
                .stream()
                .map(conducteurMapper::toResponseDTO)
                .toList();
    }


    @Override
    public ConducteurResponseDTO modifierConducteur(Long id, ConducteurRequestDTO request) {

        Conducteur conducteur = conducteurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "conducteur introuvable avec l'id " + id));

        String email = normaliserEmail(request.email());

        if (conducteurRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            log.warn("modification refusée : email déjà utilisé ({})", email);
            throw new BusinessException("cet email est déjà utilisé");
        }
        if (conducteurRepository.existsByNumeroPermisAndIdNot(request.numeroPermis(), id)) {
            log.warn("modification refusée : permis déjà utilisé ({})", request.numeroPermis());
            throw new BusinessException("ce numéro de permis est déjà utilisé");
        }

        conducteurMapper.updateEntity(request, conducteur);
        conducteur.setEmail(email);

        log.info("conducteur {} modifié", id);
        return conducteurMapper.toResponseDTO(conducteur);
    }

    private String normaliserEmail(String email) {
        return email.trim().toLowerCase();
    }
    @Override
    @Transactional(readOnly = true)
    public ConducteurResponseDTO rechercherParEmail(String email) {

        Conducteur conducteur = conducteurRepository
                .findByEmailIgnoreCase(normaliserEmail(email))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "aucun conducteur avec l'email " + email));

        return conducteurMapper.toResponseDTO(conducteur);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConducteurResponseDTO> rechercherParNom(String nom) {

        return conducteurRepository
                .findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(nom, nom)
                .stream()
                .map(conducteurMapper::toResponseDTO)
                .toList();
    }

}
