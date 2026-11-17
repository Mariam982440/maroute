package com.exemple.maroute.service.impl;

import com.exemple.maroute.mapper.VehiculeMapper;
import com.exemple.maroute.repository.ConducteurRepository;
import com.exemple.maroute.repository.VehiculeRepository;
import com.exemple.maroute.service.VehiculeService;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.logging.Logger;

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



}
