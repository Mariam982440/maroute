package com.exemple.maroute.service.impl;


import com.exemple.maroute.dto.*;
import com.exemple.maroute.entity.*;
import com.exemple.maroute.enums.StatutReservation;
import com.exemple.maroute.enums.StatutTrajet;
import com.exemple.maroute.exception.BusinessException;
import com.exemple.maroute.exception.ForbiddenException;
import com.exemple.maroute.exception.InvalidRequestException;
import com.exemple.maroute.exception.ResourceNotFoundException;
import com.exemple.maroute.mapper.TrajetMapper;
import com.exemple.maroute.repository.ConducteurRepository;
import com.exemple.maroute.repository.ReservationRepository;
import com.exemple.maroute.repository.TrajetRepository;
import com.exemple.maroute.repository.VehiculeRepository;
import com.exemple.maroute.service.TrajetService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class TrajetServiceImpl implements TrajetService {

    private static final Logger log = LoggerFactory.getLogger(TrajetServiceImpl.class);

    private static final BigDecimal PRIX_MAX_ILLIMITE = new BigDecimal("99999999.99");

    private final TrajetRepository trajetRepository;
    private final ConducteurRepository conducteurRepository;
    private final VehiculeRepository vehiculeRepository;
    private final TrajetMapper trajetMapper;
    private final ReservationRepository reservationRepository;

    public TrajetServiceImpl(
            TrajetRepository trajetRepository,
            ConducteurRepository conducteurRepository,
            VehiculeRepository vehiculeRepository,
            TrajetMapper trajetMapper,
            ReservationRepository reservationRepository) {
        this.trajetRepository = trajetRepository;
        this.conducteurRepository = conducteurRepository;
        this.vehiculeRepository = vehiculeRepository;
        this.trajetMapper = trajetMapper;
        this.reservationRepository = reservationRepository;
    }

    @Override
    public TrajetResponseDTO publierTrajet(TrajetRequestDTO request) {

        Conducteur conducteur = conducteurRepository.findById(request.conducteurId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "conducteur introuvable avec l'id " + request.conducteurId()));

        Vehicule vehicule = vehiculeRepository.findById(request.vehiculeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "véhicule introuvable avec l'id " + request.vehiculeId()));

        if (!vehicule.getConducteur().getId().equals(conducteur.getId())) {
            log.warn("publication refusée : le véhicule {} n'appartient pas au conducteur {}",
                    vehicule.getId(), conducteur.getId());
            throw new ForbiddenException("ce véhicule appartient à un autre conducteur");
        }

        if (request.placesProposees() > vehicule.getCapacite()) {
            throw refus("le nombre de places proposées ne peut pas dépasser la capacité du véhicule ("
                    + vehicule.getCapacite() + ")");
        }

        List<PointArretRequestDTO> points = request.pointsArret();
        verifierParcours(points);

        Trajet trajet = new Trajet();
        trajet.setConducteur(conducteur);
        trajet.setVehicule(vehicule);
        trajet.setPlacesProposees(request.placesProposees());
        trajet.setStatut(StatutTrajet.PUBLIE);
        trajet.setVilleDepart(points.get(0).ville().trim());
        trajet.setVilleArrivee(points.get(points.size() - 1).ville().trim());
        trajet.setDateDepart(points.get(0).heurePassage());

        for (int i = 0; i < points.size(); i++) {
            PointArretRequestDTO p = points.get(i);
            trajet.ajouterPointArret(new PointArret(
                    p.ville().trim(),
                    i,
                    p.heurePassage(),
                    p.prixDepuisDepart().setScale(2, RoundingMode.HALF_UP)));
        }

        Trajet trajetEnregistre = trajetRepository.save(trajet);
        log.info("trajet {} publié par le conducteur {} ({} -> {})",
                trajetEnregistre.getId(), conducteur.getId(),
                trajetEnregistre.getVilleDepart(), trajetEnregistre.getVilleArrivee());

        return trajetMapper.toResponseDTO(trajetEnregistre, calculerPlacesRestantes(trajetEnregistre));
    }

    @Override
    public TrajetResponseDTO consulterTrajet(Long idTrajet){

        Trajet trajet = trajetRepository.findDetailById(idTrajet)
                .orElseThrow(
                        ()->new ResourceNotFoundException("trajet introuvable avec l'id : "+idTrajet));

        return trajetMapper.toResponseDTO(trajet,calculerPlacesRestantes(trajet));
    }

    private void verifierParcours(List<PointArretRequestDTO> points){

        if (!points.get(0).heurePassage().isAfter(LocalDateTime.now())){
            throw  refus("l'heure de départ doit être dans le futur");
        }

        if (points.get(0).prixDepuisDepart().compareTo(BigDecimal.ZERO) != 0){
            throw refus("le prix du point de départ doit être égal à 0");
        }

        Set<String> villes = new HashSet<>();
        for (int i = 0; i < points.size(); i++) {
            PointArretRequestDTO point = points.get(i);

            if (!villes.add(point.ville().trim().toLowerCase())) {
                throw refus("la ville " + point.ville().trim() + " apparaît plusieurs fois sur le parcours");
            }

            if (i > 0) {
                PointArretRequestDTO precedent = points.get(i - 1);
                if (!point.heurePassage().isAfter(precedent.heurePassage())) {
                    throw refus("les heures de passage doivent être strictement croissantes");
                }
                if (point.prixDepuisDepart().compareTo(precedent.prixDepuisDepart()) <= 0) {
                    throw refus("les prix depuis le départ doivent être strictement croissants");
                }
            }
        }

    }


    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<TrajetRechercheDTO> rechercherTrajets(
            String villeDepart, String villeArrivee, LocalDate date,
            Integer nombrePlaces, BigDecimal prixMax, int page, int size) {

        if (villeDepart.isBlank() || villeArrivee.isBlank()) {
            throw refusRecherche("les villes de départ et d'arrivée sont obligatoires");
        }
        if (villeDepart.trim().equalsIgnoreCase(villeArrivee.trim())) {
            throw refusRecherche("les villes de départ et d'arrivée doivent être différentes");
        }
        if (nombrePlaces != null && nombrePlaces < 1) {
            throw refusRecherche("le nombre de places doit être au moins 1");
        }
        if (prixMax != null && prixMax.signum() < 0) {
            throw refusRecherche("le prix maximal ne peut pas être négatif");
        }
        if (page < 0) {
            throw refusRecherche("le numéro de page ne peut pas être négatif");
        }
        if (size < 1 || size > 50) {
            throw refusRecherche("la taille de page doit être comprise entre 1 et 50");
        }

        int placesDemandees = (nombrePlaces == null) ? 1 : nombrePlaces;
        BigDecimal prixMaxEffectif = (prixMax == null) ? PRIX_MAX_ILLIMITE : prixMax;

        Page<TrajetRechercheDTO> resultat = trajetRepository.rechercher(
                villeDepart.trim(),
                villeArrivee.trim(),
                date.atStartOfDay(),
                date.plusDays(1).atStartOfDay(),
                prixMaxEffectif,
                placesDemandees,
                StatutTrajet.PUBLIE,
                StatutReservation.CONFIRMEE,
                PageRequest.of(page, size));

        log.info("recherche {} -> {} le {} : {} résultat(s)",
                villeDepart.trim(), villeArrivee.trim(), date, resultat.getTotalElements());

        return PageResponseDTO.of(resultat);
    }


    @Override
    public void terminerTrajet(Long trajetId, Long conducteurId) {

        Trajet trajet = chargerTrajetDuConducteur(trajetId, conducteurId);

        verifierTrajetModifiable(trajet);

        if (LocalDateTime.now().isBefore(trajet.getDateDepart())) {
            throw conflit("le trajet ne peut être terminé qu'après son heure de départ ("
                    + trajet.getDateDepart() + ")");
        }

        trajet.setStatut(StatutTrajet.TERMINE);
        log.info("trajet {} terminé par le conducteur {}", trajetId, conducteurId);
    }


    @Override
    public void annulerTrajet(Long trajetId, Long conducteurId) {

        Trajet trajet = chargerTrajetDuConducteur(trajetId, conducteurId);

        verifierTrajetModifiable(trajet);

        List<Reservation> confirmees = reservationRepository
                .findByTrajetIdAndStatut(trajetId, StatutReservation.CONFIRMEE);
        confirmees.forEach(r -> r.setStatut(StatutReservation.ANNULEE));

        trajet.setStatut(StatutTrajet.ANNULE);
        log.info("trajet {} annulé par le conducteur {} ({} réservation(s) annulée(s))",
                trajetId, conducteurId, confirmees.size());
    }


    private Trajet chargerTrajetDuConducteur(Long trajetId, Long conducteurId) {

        Trajet trajet = trajetRepository.findByIdForUpdate(trajetId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "trajet introuvable avec l'id " + trajetId));

        if (!trajet.getConducteur().getId().equals(conducteurId)) {
            log.warn("action refusée : le trajet {} n'appartient pas au conducteur {}",
                    trajetId, conducteurId);
            throw new ForbiddenException("ce trajet appartient à un autre conducteur");
        }
        return trajet;
    }

    private InvalidRequestException refusRecherche(String message) {
        log.warn("recherche refusée : {}", message);
        return new InvalidRequestException(message);
    }


    private void verifierTrajetModifiable(Trajet trajet) {
        if (trajet.getStatut() == StatutTrajet.TERMINE || trajet.getStatut() == StatutTrajet.ANNULE) {
            throw conflit("le trajet est déjà " + trajet.getStatut()
                    + " : aucune modification n'est possible");
        }
    }

    private BusinessException conflit(String message) {
        log.warn("opération refusée : {}", message);
        return new BusinessException(message);
    }


    private int calculerPlacesRestantes(Trajet trajet) {
        return trajet.getPlacesProposees();
    }

    private InvalidRequestException refus(String message){
        log.warn("publication refusée: {}",message);
        throw new InvalidRequestException(message);
    }



   

}