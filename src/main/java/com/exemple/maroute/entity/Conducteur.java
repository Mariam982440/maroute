package com.exemple.maroute.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(
        name = "conducteurs",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_conducteur_email",
                        columnNames = "email"
                ),
                @UniqueConstraint(
                        name = "uk_conducteur_numero_permis",
                        columnNames = "numero_permis"
                )
        }
)
public class Conducteur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String prenom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'adresse email doit être valide")
    @Column(nullable = false, length = 150)
    private String email;

    @NotBlank(message = "Le téléphone est obligatoire")
    @Size(max = 20)
    @Column(nullable = false, length = 20)
    private String telephone;

    @NotBlank(message = "Le numéro de permis est obligatoire")
    @Size(max = 50)
    @Column(name = "numero_permis", nullable = false, length = 50)
    private String numeroPermis;

    public Conducteur() {
    }

    public Conducteur(String nom, String prenom, String email,
                      String telephone, String numeroPermis) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.numeroPermis = numeroPermis;
    }

    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getNumeroPermis() {
        return numeroPermis;
    }

    public void setNumeroPermis(String numeroPermis) {
        this.numeroPermis = numeroPermis;
    }
}
