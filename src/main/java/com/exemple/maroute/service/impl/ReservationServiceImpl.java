package com.exemple.maroute.service.impl;

import com.exemple.maroute.dto.ReservationResponseDTO;
import com.exemple.maroute.entity.Passager;
import com.exemple.maroute.mapper.ReservationMapper;
import com.exemple.maroute.repository.PassagerRepository;
import com.exemple.maroute.repository.ReservationRepository;
import com.exemple.maroute.repository.TrajetRepository;
import com.exemple.maroute.service.ReservationService;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.logging.Logger;

@Service
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private static final Logger log = LoggerFactory.getLogger(ReservationServiceImpl.class);

    private static final long DELAI_ANNULATION_HEURES = 24;

    private final ReservationRepository reservationRepository;
    private final PassagerRepository passagerRepository;
    private final TrajetRepository trajetRepository;
    private final ReservationMapper reservationMapper;

    public ReservationServiceImpl(
            ReservationRepository reservationRepository,
            PassagerRepository passagerRepository,
            TrajetRepository trajetRepository,
            ReservationMapper reservationMapper) {
        this.reservationRepository = reservationRepository;
        this.passagerRepository = passagerRepository;
        this.trajetRepository = trajetRepository;
        this.reservationMapper = reservationMapper;
    }


}
