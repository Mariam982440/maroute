package com.exemple.maroute.service.impl;


import com.exemple.maroute.dto.PassagerRequestDTO;
import com.exemple.maroute.dto.PassagerResponseDTO;
import com.exemple.maroute.dto.VehiculeRequestDTO;
import com.exemple.maroute.dto.VehiculeResponseDTO;
import com.exemple.maroute.entity.Passager;
import com.exemple.maroute.exception.BusinessException;
import com.exemple.maroute.exception.ResourceNotFoundException;
import com.exemple.maroute.mapper.PassagerMapper;
import com.exemple.maroute.repository.PassagerRepository;
import com.exemple.maroute.service.PassagerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
@Transactional
public class PassagerServiceImpl implements PassagerService {

    private static final Logger log = LoggerFactory.getLogger(PassagerServiceImpl.class);

    private final PassagerRepository passagerRepository;
    private final PassagerMapper passagerMapper;

    public PassagerServiceImpl(
            PassagerRepository passagerRepository,
            PassagerMapper passagerMapper) {
        this.passagerRepository = passagerRepository;
        this.passagerMapper = passagerMapper;
    }
    @Override
    public PassagerResponseDTO crerPassager(PassagerRequestDTO request){

        String email = normaliserEmail(request.email());

        if (passagerRepository.existsByEmailIgnoreCase(email)) {
            log.warn("création refusée : email déjà utilisé ({})", email);
            throw new BusinessException("cet email est déjà utilisé");
        }

        Passager passager = passagerMapper.toEntity(request);
        passager.setEmail(email);

        Passager passagerEnregistre = passagerRepository.save(passager);
        log.info("passager créé avec l'id {}", passagerEnregistre.getId());

        return passagerMapper.toResponseDTO(passagerEnregistre);
    }

    private String normaliserEmail(String email) {
        return email.trim().toLowerCase();
    }

    @Override
    @Transactional(readOnly = true)
    public PassagerResponseDTO trouverPassagerParId(Long id) {

        Passager passager = passagerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "passager introuvable avec l'id " + id));

        return passagerMapper.toResponseDTO(passager);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PassagerResponseDTO> trouverTousLesPassagers() {

        return passagerRepository.findAll()
                .stream()
                .map(passagerMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PassagerResponseDTO rechercherParEmail(String email) {

        Passager passager = passagerRepository
                .findByEmailIgnoreCase(normaliserEmail(email))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "aucun passager avec l'email " + email));

        return passagerMapper.toResponseDTO(passager);
    }

}