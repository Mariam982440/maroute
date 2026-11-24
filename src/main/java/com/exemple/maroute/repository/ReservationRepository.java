package com.exemple.maroute.repository;

import com.exemple.maroute.entity.Reservation;
import com.exemple.maroute.enums.StatutReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("""
            select coalesce(sum(r.nombrePlaces), 0)
            from Reservation r
            where r.trajet.id = :trajetId and r.statut = :statut
            """)
    Long sommePlacesParTrajetEtStatut(@Param("trajetId") Long trajetId,
                                      @Param("statut") StatutReservation statut);

    boolean existsByPassagerIdAndTrajetIdAndStatut(Long passagerId, Long trajetId,
                                                   StatutReservation statut);

    List<Reservation> findByTrajetIdAndStatut(Long trajetId, StatutReservation statut);

    @Query("""
            select r from Reservation r
            join fetch r.pointMontee
            join fetch r.pointDescente
            where r.passager.id = :passagerId
            order by r.dateReservation desc
            """)
    List<Reservation> findHistoriqueByPassagerId(@Param("passagerId") Long passagerId);

    @Query("""
            select r from Reservation r
            join fetch r.pointMontee
            join fetch r.pointDescente
            where r.trajet.id = :trajetId
            order by r.dateReservation desc
            """)
    List<Reservation> findByTrajetIdAvecPoints(@Param("trajetId") Long trajetId);
}
