package com.exemple.maroute.repository;

import com.exemple.maroute.dto.TrajetRechercheDTO;
import com.exemple.maroute.dto.TrajetResponseDTO;
import com.exemple.maroute.enums.StatutReservation;
import com.exemple.maroute.enums.StatutTrajet;
import com.exemple.maroute.entity.Trajet;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TrajetRepository extends JpaRepository<Trajet, Long> {

    List<Trajet> findByConducteurId(Long conducteurId);

    @Query("""
            select t from Trajet t
            join fetch t.conducteur
            join fetch t.vehicule
            left join fetch t.pointsArret
            where t.id = :id
            """)
    Optional<Trajet> findDetailById(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from Trajet t where t.id = :id")
    Optional<Trajet> findByIdForUpdate(@Param("id") Long id);



    @Query(value = """
            select new com.exemple.maroute.dto.TrajetRechercheDTO(
                t.id,
                c.id, c.prenom, c.nom,
                pd.id, pd.ville, pd.heurePassage,
                pa.id, pa.ville, pa.heurePassage,
                (pa.prixDepuisDepart - pd.prixDepuisDepart),
                (t.placesProposees - coalesce(
                    (select sum(r.nombrePlaces) from Reservation r
                      where r.trajet = t and r.statut = :confirmee), 0L)),
                t.dateDepart
            )
            from Trajet t
            join t.conducteur c
            join t.pointsArret pd
            join t.pointsArret pa
            where t.statut = :statut
              and lower(pd.ville) = lower(:villeDepart)
              and lower(pa.ville) = lower(:villeArrivee)
              and pd.ordre < pa.ordre
              and t.dateDepart >= :debut and t.dateDepart < :fin
              and (pa.prixDepuisDepart - pd.prixDepuisDepart) <= :prixMax
              and (t.placesProposees - coalesce(
                    (select sum(r2.nombrePlaces) from Reservation r2
                      where r2.trajet = t and r2.statut = :confirmee), 0L)) >= :nombrePlaces
            order by t.dateDepart asc
            """,
            countQuery = """
            select count(t)
            from Trajet t
            join t.pointsArret pd
            join t.pointsArret pa
            where t.statut = :statut
              and lower(pd.ville) = lower(:villeDepart)
              and lower(pa.ville) = lower(:villeArrivee)
              and pd.ordre < pa.ordre
              and t.dateDepart >= :debut and t.dateDepart < :fin
              and (pa.prixDepuisDepart - pd.prixDepuisDepart) <= :prixMax
              and (t.placesProposees - coalesce(
                    (select sum(r2.nombrePlaces) from Reservation r2
                      where r2.trajet = t and r2.statut = :confirmee), 0L)) >= :nombrePlaces
            """)
    Page<TrajetRechercheDTO> rechercher(@Param("villeDepart") String villeDepart,
                                        @Param("villeArrivee") String villeArrivee,
                                        @Param("debut") LocalDateTime debut,
                                        @Param("fin") LocalDateTime fin,
                                        @Param("prixMax") BigDecimal prixMax,
                                        @Param("nombrePlaces") int nombrePlaces,
                                        @Param("statut") StatutTrajet statut,
                                        @Param("confirmee") StatutReservation confirmee,
                                        Pageable pageable);
}
