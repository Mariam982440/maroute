package com.exemple.maroute.service.impl;

import com.exemple.maroute.dto.PageResponseDTO;
import com.exemple.maroute.dto.PointArretRequestDTO;
import com.exemple.maroute.dto.TrajetRechercheDTO;
import com.exemple.maroute.dto.TrajetRequestDTO;
import com.exemple.maroute.dto.TrajetResponseDTO;
import com.exemple.maroute.entity.Conducteur;
import com.exemple.maroute.entity.PointArret;
import com.exemple.maroute.entity.Reservation;
import com.exemple.maroute.entity.Trajet;
import com.exemple.maroute.entity.Vehicule;
import com.exemple.maroute.enums.StatutReservation;
import com.exemple.maroute.enums.StatutTrajet;
import com.exemple.maroute.exception.BusinessException;
import com.exemple.maroute.exception.ForbiddenException;
import com.exemple.maroute.exception.InvalidRequestException;
import com.exemple.maroute.mapper.TrajetMapper;
import com.exemple.maroute.repository.ConducteurRepository;
import com.exemple.maroute.repository.ReservationRepository;
import com.exemple.maroute.repository.TrajetRepository;
import com.exemple.maroute.repository.VehiculeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrajetServiceImplTest {

    @Mock
    private TrajetRepository trajetRepository;
    @Mock
    private ConducteurRepository conducteurRepository;
    @Mock
    private VehiculeRepository vehiculeRepository;
    @Mock
    private TrajetMapper trajetMapper;
    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private TrajetServiceImpl service;

    private Conducteur conducteur;
    private Vehicule vehicule;
    private TrajetRequestDTO request;

    @BeforeEach
    void setUp() {
        conducteur = new Conducteur();
        ReflectionTestUtils.setField(conducteur, "id", 1L);

        vehicule = new Vehicule("Peugeot", "308", "AA-123-BB", "gris", 4, conducteur);
        ReflectionTestUtils.setField(vehicule, "id", 5L);

        request = new TrajetRequestDTO(1L, 5L, 3, List.of(
                point("Rabat", LocalDateTime.now().plusDays(2), "0.00"),
                point("Casablanca", LocalDateTime.now().plusDays(2).plusHours(2), "25.00")));
    }

    private PointArretRequestDTO point(String ville, LocalDateTime heure, String prix) {
        return new PointArretRequestDTO(ville, heure, new BigDecimal(prix));
    }

    private Trajet trajet(StatutTrajet statut, LocalDateTime dateDepart) {
        Trajet t = new Trajet();
        ReflectionTestUtils.setField(t, "id", 20L);
        t.setConducteur(conducteur);
        t.setVehicule(vehicule);
        t.setStatut(statut);
        t.setDateDepart(dateDepart);
        t.setPlacesProposees(3);
        t.setVilleDepart("Rabat");
        t.setVilleArrivee("Casablanca");
        return t;
    }

    @Test
    void publierTrajet_creeLeTrajetAvecSesPoints() {
        when(conducteurRepository.findById(1L)).thenReturn(Optional.of(conducteur));
        when(vehiculeRepository.findById(5L)).thenReturn(Optional.of(vehicule));
        when(trajetRepository.save(any(Trajet.class))).thenAnswer(invocation -> {
            Trajet t = invocation.getArgument(0);
            ReflectionTestUtils.setField(t, "id", 20L);
            return t;
        });
        when(trajetMapper.toResponseDTO(any(Trajet.class), anyInt()))
                .thenReturn(new TrajetResponseDTO(20L, null, null, "Rabat", "Casablanca",
                        LocalDateTime.now().plusDays(2), 3, 3, StatutTrajet.PUBLIE, List.of()));

        TrajetResponseDTO resultat = service.publierTrajet(request);

        ArgumentCaptor<Trajet> captor = ArgumentCaptor.forClass(Trajet.class);
        verify(trajetRepository).save(captor.capture());
        Trajet enregistre = captor.getValue();
        assertThat(enregistre.getVilleDepart()).isEqualTo("Rabat");
        assertThat(enregistre.getVilleArrivee()).isEqualTo("Casablanca");
        assertThat(enregistre.getStatut()).isEqualTo(StatutTrajet.PUBLIE);
        assertThat(enregistre.getPointsArret()).hasSize(2);
        assertThat(enregistre.getPointsArret().get(1).getPrixDepuisDepart())
                .isEqualByComparingTo("25.00");
        assertThat(resultat.placesRestantes()).isEqualTo(3);
        verify(trajetMapper).toResponseDTO(enregistre, 3);
    }

    @Test
    void publierTrajet_refuseUnVehiculeDUnAutreConducteur() {
        Conducteur autre = new Conducteur();
        ReflectionTestUtils.setField(autre, "id", 999L);
        vehicule.setConducteur(autre);
        when(conducteurRepository.findById(1L)).thenReturn(Optional.of(conducteur));
        when(vehiculeRepository.findById(5L)).thenReturn(Optional.of(vehicule));

        assertThatThrownBy(() -> service.publierTrajet(request))
                .isInstanceOf(ForbiddenException.class);

        verify(trajetRepository, never()).save(any());
    }

    @Test
    void publierTrajet_refuseTropDePlacesParRapportALaCapacite() {
        TrajetRequestDTO trop = new TrajetRequestDTO(1L, 5L, 5, request.pointsArret());
        when(conducteurRepository.findById(1L)).thenReturn(Optional.of(conducteur));
        when(vehiculeRepository.findById(5L)).thenReturn(Optional.of(vehicule));

        assertThatThrownBy(() -> service.publierTrajet(trop))
                .isInstanceOf(InvalidRequestException.class);

        verify(trajetRepository, never()).save(any());
    }

    @Test
    void publierTrajet_refuseUnDepartDansLePasse() {
        TrajetRequestDTO passe = new TrajetRequestDTO(1L, 5L, 3, List.of(
                point("Rabat", LocalDateTime.now().minusHours(1), "0.00"),
                point("Casablanca", LocalDateTime.now().plusHours(1), "25.00")));
        when(conducteurRepository.findById(1L)).thenReturn(Optional.of(conducteur));
        when(vehiculeRepository.findById(5L)).thenReturn(Optional.of(vehicule));

        assertThatThrownBy(() -> service.publierTrajet(passe))
                .isInstanceOf(InvalidRequestException.class);

        verify(trajetRepository, never()).save(any());
    }

    @Test
    void terminerTrajet_passeEnTermine() {
        Trajet t = trajet(StatutTrajet.PUBLIE, LocalDateTime.now().minusHours(1));
        when(trajetRepository.findByIdForUpdate(20L)).thenReturn(Optional.of(t));

        service.terminerTrajet(20L, 1L);

        assertThat(t.getStatut()).isEqualTo(StatutTrajet.TERMINE);
    }

    @Test
    void terminerTrajet_refuseUnAutreConducteur() {
        Trajet t = trajet(StatutTrajet.PUBLIE, LocalDateTime.now().minusHours(1));
        when(trajetRepository.findByIdForUpdate(20L)).thenReturn(Optional.of(t));

        assertThatThrownBy(() -> service.terminerTrajet(20L, 999L))
                .isInstanceOf(ForbiddenException.class);

        assertThat(t.getStatut()).isEqualTo(StatutTrajet.PUBLIE);
    }

    @Test
    void terminerTrajet_refuseAvantLHeureDeDepart() {
        Trajet t = trajet(StatutTrajet.PUBLIE, LocalDateTime.now().plusHours(2));
        when(trajetRepository.findByIdForUpdate(20L)).thenReturn(Optional.of(t));

        assertThatThrownBy(() -> service.terminerTrajet(20L, 1L))
                .isInstanceOf(BusinessException.class);

        assertThat(t.getStatut()).isEqualTo(StatutTrajet.PUBLIE);
    }

    @Test
    void annulerTrajet_annuleAussiLesReservationsConfirmees() {
        Trajet t = trajet(StatutTrajet.PUBLIE, LocalDateTime.now().plusDays(2));
        PointArret montee = new PointArret("Rabat", 0, t.getDateDepart(), BigDecimal.ZERO);
        PointArret descente = new PointArret("Casablanca", 1, t.getDateDepart(), new BigDecimal("25.00"));
        Reservation r1 = new Reservation(null, t, montee, descente, 1, BigDecimal.TEN,
                LocalDateTime.now(), StatutReservation.CONFIRMEE);
        Reservation r2 = new Reservation(null, t, montee, descente, 2, BigDecimal.TEN,
                LocalDateTime.now(), StatutReservation.CONFIRMEE);
        when(trajetRepository.findByIdForUpdate(20L)).thenReturn(Optional.of(t));
        when(reservationRepository.findByTrajetIdAndStatut(20L, StatutReservation.CONFIRMEE))
                .thenReturn(List.of(r1, r2));

        service.annulerTrajet(20L, 1L);

        assertThat(t.getStatut()).isEqualTo(StatutTrajet.ANNULE);
        assertThat(r1.getStatut()).isEqualTo(StatutReservation.ANNULEE);
        assertThat(r2.getStatut()).isEqualTo(StatutReservation.ANNULEE);
    }

    @Test
    void rechercherTrajets_refuseUnePageNegative() {
        assertThatThrownBy(() -> service.rechercherTrajets("Rabat", "Casablanca",
                LocalDate.now().plusDays(2), 1, null, -1, 10))
                .isInstanceOf(InvalidRequestException.class);

        verify(trajetRepository, never()).rechercher(any(), any(), any(), any(), any(),
                anyInt(), any(), any(), any());
    }

    @Test
    void rechercherTrajets_transmetLesCriteresAuRepository() {
        when(trajetRepository.rechercher(eq("Rabat"), eq("Casablanca"), any(), any(),
                eq(new BigDecimal("100")), eq(2), eq(StatutTrajet.PUBLIE),
                eq(StatutReservation.CONFIRMEE), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.<TrajetRechercheDTO>of()));

        PageResponseDTO<TrajetRechercheDTO> resultat = service.rechercherTrajets(
                "Rabat", "Casablanca", LocalDate.now().plusDays(2), 2,
                new BigDecimal("100"), 0, 10);

        assertThat(resultat.totalElements()).isZero();
        verify(trajetRepository).rechercher(eq("Rabat"), eq("Casablanca"), any(), any(),
                eq(new BigDecimal("100")), eq(2), eq(StatutTrajet.PUBLIE),
                eq(StatutReservation.CONFIRMEE), any(Pageable.class));
    }
}
