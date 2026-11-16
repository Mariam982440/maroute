package com.exemple.maroute.repository;

import com.exemple.maroute.entity.Vehicule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehiculeRepository extends JpaRepository<Vehicule, Long> {
}
