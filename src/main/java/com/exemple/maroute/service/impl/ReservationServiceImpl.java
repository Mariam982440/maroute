package com.exemple.maroute.service.impl;

import com.exemple.maroute.dto.ReservationRequestDTO;
import com.exemple.maroute.dto.ReservationResponseDTO;
import com.exemple.maroute.entity.Passager;
import com.exemple.maroute.entity.PointArret;
import com.exemple.maroute.entity.Reservation;
import com.exemple.maroute.entity.Trajet;
import com.exemple.maroute.enums.StatutReservation;
import com.exemple.maroute.enums.StatutTrajet;
import com.exemple.maroute.exception.BusinessException;
import com.exemple.maroute.exception.ForbiddenException;
import com.exemple.maroute.exception.InvalidRequestException;
import com.exemple.maroute.exception.ResourceNotFoundException;
import com.exemple.maroute.mapper.ReservationMapper;
import com.exemple.maroute.repository.PassagerRepository;
import com.exemple.maroute.repository.ReservationRepository;
import com.exemple.maroute.repository.TrajetRepository;
import com.exemple.maroute.service.ReservationService;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
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

    @Override
    public ReservationResponseDTO reserver(ReservationRequestDTO request) {

        Passager passager = passagerRepository.findById(request.passagerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "passager introuvable avec l'id " + request.passagerId()));

        Trajet trajet = trajetRepository.findByIdForUpdate(request.trajetId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "trajet introuvable avec l'id " + request.trajetId()));


        PointArret montee = trouverPoint(trajet, request.pointMonteeId(), "montée");
        PointArret descente = trouverPoint(trajet, request.pointDescenteId(), "descente");

        if (montee.getOrdre() >= descente.getOrdre()) {
            throw refus("la montée doit précéder la descente");
        }

        if (trajet.getStatut() == StatutTrajet.COMPLET) {
            throw conflit("le trajet est complet");
        }
        if (trajet.getStatut() != StatutTrajet.PUBLIE) {
            throw conflit("le trajet n'est plus ouvert à la réservation (statut "
                    + trajet.getStatut() + ")");
        }

        if (reservationRepository.existsByPassagerIdAndTrajetIdAndStatut(
                passager.getId(), trajet.getId(), StatutReservation.CONFIRMEE)) {
            throw conflit("ce passager a déjà une réservation confirmée sur ce trajet");
        }

        int placesRestantes = calculerPlacesRestantes(trajet);
        if (request.nombrePlaces() > placesRestantes) {
            throw conflit("il ne reste que " + placesRestantes + " place(s) sur ce trajet");
        }


        BigDecimal prixPlace = descente.getPrixDepuisDepart().subtract(montee.getPrixDepuisDepart());
        BigDecimal montantTotal = prixPlace
                .multiply(BigDecimal.valueOf(request.nombrePlaces()))
                .setScale(2, RoundingMode.HALF_UP);

        Reservation reservation = new Reservation(
                passager, trajet, montee, descente,
                request.nombrePlaces(), montantTotal,
                LocalDateTime.now(), StatutReservation.CONFIRMEE);

        Reservation enregistree = reservationRepository.save(reservation);

        if (placesRestantes - request.nombrePlaces() == 0) {
            trajet.setStatut(StatutTrajet.COMPLET);
            log.info("trajet {} devenu COMPLET", trajet.getId());
        }

        log.info("réservation {} confirmée : passager {}, trajet {}, {} place(s), {} DH",
                enregistree.getId(), passager.getId(), trajet.getId(),
                request.nombrePlaces(), montantTotal);

        return reservationMapper.toResponseDTO(enregistree);
    }



    @Override
    public void annulerReservation(Long reservationId, Long passagerId) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "réservation introuvable avec l'id " + reservationId));

        if (!reservation.getPassager().getId().equals(passagerId)) {
            log.warn("annulation refusée : la réservation {} n'appartient pas au passager {}",
                    reservationId, passagerId);
            throw new ForbiddenException("cette réservation appartient à un autre passager");
        }

        if (reservation.getStatut() != StatutReservation.CONFIRMEE) {
            throw conflit("cette réservation est déjà annulée");
        }

        Trajet trajet = trajetRepository.findByIdForUpdate(reservation.getTrajet().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "trajet introuvable avec l'id " + reservation.getTrajet().getId()));

        // annulation possible jusqu'à 24 h avant le départ
        LocalDateTime limite = trajet.getDateDepart().minusHours(DELAI_ANNULATION_HEURES);
        if (LocalDateTime.now().isAfter(limite)) {
            throw conflit("annulation impossible : le délai de " + DELAI_ANNULATION_HEURES
                    + " h avant le départ est dépassé");
        }

        reservation.setStatut(StatutReservation.ANNULEE);

        // une place vient de se libérer
        if (trajet.getStatut() == StatutTrajet.COMPLET) {
            trajet.setStatut(StatutTrajet.PUBLIE);
            log.info("trajet {} redevenu PUBLIE", trajet.getId());
        }

        log.info("réservation {} annulée par le passager {}", reservationId, passagerId);
    }


    private int calculerPlacesRestantes(Trajet trajet) {
        long reservees = reservationRepository.sommePlacesParTrajetEtStatut(
                trajet.getId(), StatutReservation.CONFIRMEE);
        return trajet.getPlacesProposees() - (int) reservees;
    }

    private PointArret trouverPoint(Trajet trajet, Long pointId, String role) {
        return trajet.getPointsArret().stream()
                .filter(p -> p.getId().equals(pointId))
                .findFirst()
                .orElseThrow(() -> refus("le point de " + role + " " + pointId
                        + " n'appartient pas au trajet " + trajet.getId()));
    }

    private InvalidRequestException refus(String message) {
        log.warn("réservation refusée : {}", message);
        return new InvalidRequestException(message);
    }

    private BusinessException conflit(String message) {
        log.warn("opération refusée : {}", message);
        return new BusinessException(message);
    }
}
