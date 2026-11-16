package com.exemple.maroute.service.impl;

import com.exemple.maroute.dto.ConducteurRequestDTO;
import com.exemple.maroute.dto.ConducteurResponseDTO;
import com.exemple.maroute.entity.Conducteur;
import com.exemple.maroute.mapper.ConducteurMapper;
import com.exemple.maroute.repository.ConducteurRepository;
import com.exemple.maroute.service.ConducteurService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ConducteurServiceImpl implements ConducteurService {

    private final ConducteurRepository conducteurRepository;
    private final ConducteurMapper conducteurMapper;

    public ConducteurServiceImpl(
            ConducteurRepository conducteurRepository,
            ConducteurMapper conducteurMapper) {
        this.conducteurRepository = conducteurRepository;
        this.conducteurMapper = conducteurMapper;
    }
    @Override
    public ConducteurResponseDTO creerConducteur(ConducteurRequestDTO request){
        if (conducteurRepository.existsByEmailIgnoreCase(request.email())) {
            throw new RuntimeException("cet email est déjà utilisé");
        }
        if (conducteurRepository.existsByNumeroPermis(request.numeroPermis())) {
            throw new RuntimeException("ce numéro de permis est déjà utilisé");
        }

        Conducteur conducteur = conducteurMapper.toEntity(request);

        Conducteur conducteurEnregistre = conducteurRepository.save(conducteur);

        return conducteurMapper.toResponseDTO(conducteurEnregistre);
    }

    @Override
    @Transactional(readOnly = true)
    public ConducteurResponseDTO trouverConducteurParId(Long id) {

        Conducteur conducteur = conducteurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("conducteur introuvable"));

        return conducteurMapper.toResponseDTO(conducteur);
    }

}
