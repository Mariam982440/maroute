package com.exemple.maroute.service.impl;

import com.exemple.maroute.dto.ReservationRequestDTO;
import com.exemple.maroute.dto.ReservationResponseDTO;
import com.exemple.maroute.entity.Conducteur;
import com.exemple.maroute.entity.Passager;
import com.exemple.maroute.entity.PointArret;
import com.exemple.maroute.entity.Reservation;
import com.exemple.maroute.entity.Trajet;
import com.exemple.maroute.enums.StatutReservation;
import com.exemple.maroute.enums.StatutTrajet;
import com.exemple.maroute.exception.BusinessException;
import com.exemple.maroute.exception.ForbiddenException;
import com.exemple.maroute.exception.InvalidRequestException;
import com.exemple.maroute.mapper.ReservationMapper;
import com.exemple.maroute.repository.PassagerRepository;
import com.exemple.maroute.repository.ReservationRepository;
import com.exemple.maroute.repository.TrajetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private PassagerRepository passagerRepository;
    @Mock
    private TrajetRepository trajetRepository;
    @Mock
    private ReservationMapper reservationMapper;

    @InjectMocks
    private ReservationServiceImpl service;

    private Passager passager;
    private Trajet trajet;
    private PointArret montee;
    private PointArret descente;
    private ReservationRequestDTO request;

    @BeforeEach
    void setUp() {
        passager = new Passager("Doe", "John", "john@test.com", "0600000000");
        ReflectionTestUtils.setField(passager, "id", 1L);

        trajet = new Trajet();
        ReflectionTestUtils.setField(trajet, "id", 10L);
        trajet.setStatut(StatutTrajet.PUBLIE);
        trajet.setPlacesProposees(4);
        trajet.setDateDepart(LocalDateTime.now().plusDays(3));

        montee = point(1L, "Rabat", 0, "0.00");
        descente = point(2L, "Casablanca", 1, "25.00");
        trajet.ajouterPointArret(montee);
        trajet.ajouterPointArret(descente);

        request = new ReservationRequestDTO(1L, 10L, 1L, 2L, 2);
    }

    private PointArret point(Long id, String ville, int ordre, String prix) {
        PointArret p = new PointArret(ville, ordre, LocalDateTime.now().plusDays(3), new BigDecimal(prix));
        ReflectionTestUtils.setField(p, "id", id);
        return p;
    }

    private Reservation reservation(Long id, StatutReservation statut) {
        Reservation r = new Reservation(passager, trajet, montee, descente, 1,
                new BigDecimal("25.00"), LocalDateTime.now(), statut);
        ReflectionTestUtils.setField(r, "id", id);
        return r;
    }

    @Test
    void reserver_calculLePrixEtConfirme() {
        when(passagerRepository.findById(1L)).thenReturn(Optional.of(passager));
        when(trajetRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(trajet));
        when(reservationRepository.existsByPassagerIdAndTrajetIdAndStatut(
                1L, 10L, StatutReservation.CONFIRMEE)).thenReturn(false);
        when(reservationRepository.sommePlacesParTrajetEtStatut(10L, StatutReservation.CONFIRMEE))
                .thenReturn(0L);
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation r = invocation.getArgument(0);
            ReflectionTestUtils.setField(r, "id", 100L);
            return r;
        });
        when(reservationMapper.toResponseDTO(any(Reservation.class)))
                .thenReturn(new ReservationResponseDTO(100L, 1L, 10L, 1L, "Rabat", 2L,
                        "Casablanca", 2, new BigDecimal("50.00"), LocalDateTime.now(),
                        StatutReservation.CONFIRMEE));

        ReservationResponseDTO resultat = service.reserver(request);

        ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);
        verify(reservationRepository).save(captor.capture());
        Reservation enregistree = captor.getValue();
        assertThat(enregistree.getNombrePlaces()).isEqualTo(2);
        assertThat(enregistree.getMontantTotal()).isEqualByComparingTo("50.00");
        assertThat(enregistree.getStatut()).isEqualTo(StatutReservation.CONFIRMEE);
        assertThat(resultat.montantTotal()).isEqualByComparingTo("50.00");
        assertThat(trajet.getStatut()).isEqualTo(StatutTrajet.PUBLIE);
    }

    @Test
    void reserver_refuseUnTrajetComplet() {
        trajet.setStatut(StatutTrajet.COMPLET);
        when(passagerRepository.findById(1L)).thenReturn(Optional.of(passager));
        when(trajetRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(trajet));

        assertThatThrownBy(() -> service.reserver(request))
                .isInstanceOf(BusinessException.class);

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void reserver_refuseUneMonteeApresLaDescente() {
        ReservationRequestDTO invalide = new ReservationRequestDTO(1L, 10L, 2L, 1L, 1);
        when(passagerRepository.findById(1L)).thenReturn(Optional.of(passager));
        when(trajetRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(trajet));

        assertThatThrownBy(() -> service.reserver(invalide))
                .isInstanceOf(InvalidRequestException.class);

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void reserver_refuseSiPasAssezDePlaces() {
        ReservationRequestDTO trop = new ReservationRequestDTO(1L, 10L, 1L, 2L, 5);
        when(passagerRepository.findById(1L)).thenReturn(Optional.of(passager));
        when(trajetRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(trajet));
        when(reservationRepository.existsByPassagerIdAndTrajetIdAndStatut(
                1L, 10L, StatutReservation.CONFIRMEE)).thenReturn(false);
        when(reservationRepository.sommePlacesParTrajetEtStatut(10L, StatutReservation.CONFIRMEE))
                .thenReturn(2L);

        assertThatThrownBy(() -> service.reserver(trop))
                .isInstanceOf(BusinessException.class);

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void annulerReservation_passeEnAnnuleeEtRendLeTrajetDisponible() {
        Reservation r = reservation(50L, StatutReservation.CONFIRMEE);
        trajet.setStatut(StatutTrajet.COMPLET);
        when(reservationRepository.findById(50L)).thenReturn(Optional.of(r));
        when(trajetRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(trajet));

        service.annulerReservation(50L, 1L);

        assertThat(r.getStatut()).isEqualTo(StatutReservation.ANNULEE);
        assertThat(trajet.getStatut()).isEqualTo(StatutTrajet.PUBLIE);
    }

    @Test
    void annulerReservation_refuseApresLeDelai() {
        Reservation r = reservation(50L, StatutReservation.CONFIRMEE);
        trajet.setDateDepart(LocalDateTime.now().plusHours(10));
        when(reservationRepository.findById(50L)).thenReturn(Optional.of(r));
        when(trajetRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(trajet));

        assertThatThrownBy(() -> service.annulerReservation(50L, 1L))
                .isInstanceOf(BusinessException.class);

        assertThat(r.getStatut()).isEqualTo(StatutReservation.CONFIRMEE);
    }

    @Test
    void annulerReservation_refuseUnAutrePassager() {
        Reservation r = reservation(50L, StatutReservation.CONFIRMEE);
        when(reservationRepository.findById(50L)).thenReturn(Optional.of(r));

        assertThatThrownBy(() -> service.annulerReservation(50L, 999L))
                .isInstanceOf(ForbiddenException.class);

        verify(trajetRepository, never()).findByIdForUpdate(any());
    }

    @Test
    void consulterReservationsTrajet_refuseUnAutreConducteur() {
        Conducteur conducteur = new Conducteur();
        ReflectionTestUtils.setField(conducteur, "id", 3L);
        trajet.setConducteur(conducteur);
        when(trajetRepository.findById(10L)).thenReturn(Optional.of(trajet));

        assertThatThrownBy(() -> service.consulterReservationsTrajet(10L, 999L))
                .isInstanceOf(ForbiddenException.class);

        verify(reservationRepository, never()).findByTrajetIdAvecPoints(any());
    }

    @Test
    void consulterReservationsPassager_retourneLHistorique() {
        Reservation r = reservation(50L, StatutReservation.CONFIRMEE);
        when(passagerRepository.existsById(1L)).thenReturn(true);
        when(reservationRepository.findHistoriqueByPassagerId(1L)).thenReturn(List.of(r));
        when(reservationMapper.toResponseDTO(r))
                .thenReturn(new ReservationResponseDTO(50L, 1L, 10L, 1L, "Rabat", 2L,
                        "Casablanca", 1, new BigDecimal("25.00"), r.getDateReservation(),
                        StatutReservation.CONFIRMEE));

        List<ReservationResponseDTO> resultat = service.consulterReservationsPassager(1L);

        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).id()).isEqualTo(50L);
    }
}
