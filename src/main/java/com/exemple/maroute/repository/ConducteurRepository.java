package com.exemple.maroute.repository;

import com.exemple.maroute.entity.Conducteur;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConducteurRepository extends JpaRepository<Conducteur, Long> {
    boolean existsByEmailIgnoreCase(String email);

    boolean existsByNumeroPermis(String s);

    boolean existsByNumeroPermisAndIdNot(String s, Long id);

    boolean existsByEmailIgnoreCaseAndIdNot(@NotBlank(message = "l'email est obligatoire") @Email(message = "l'adresse email doit être valide") @Size(max = 150, message = "l'email ne doit pas dépasser 150 caractères") String email, Long id);
}
