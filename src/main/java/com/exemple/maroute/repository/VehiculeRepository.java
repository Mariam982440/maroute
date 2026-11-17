package com.exemple.maroute.repository;

import com.exemple.maroute.entity.Vehicule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehiculeRepository extends JpaRepository<Vehicule, Long> {

    boolean existsByImmatriculationIgnoreCase(String immatriculation);

    boolean existsByImmatriculationIgnoreCaseAndIdNot(String immatriculation, Long id);

    List<Vehicule> findByConducteurId(Long conducteurId);
}
