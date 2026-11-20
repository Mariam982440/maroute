package com.exemple.maroute.repository;

import com.exemple.maroute.entity.Trajet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface  TrajetRepository extends JpaRepository<Trajet, Long> {

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
}
