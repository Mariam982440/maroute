package com.exemple.maroute.repository;

import com.exemple.maroute.entity.Conducteur;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConducteurRepository extends JpaRepository<Conducteur, Long> {
    boolean existsByEmailIgnoreCase(String email);

    boolean existsByNumeroPermis(String s);
}
