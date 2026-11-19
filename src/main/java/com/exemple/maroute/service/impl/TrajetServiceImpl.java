package com.exemple.maroute.service.impl;


import com.exemple.maroute.mapper.TrajetMapper;
import com.exemple.maroute.repository.ConducteurRepository;
import com.exemple.maroute.repository.TrajetRepository;
import com.exemple.maroute.repository.VehiculeRepository;
import com.exemple.maroute.service.TrajetService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

}