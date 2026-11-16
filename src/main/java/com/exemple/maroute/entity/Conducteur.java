package com.exemple.maroute.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

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

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, length = 100)
    private String prenom;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false, length = 20)
    private String telephone;

    @Column(name = "numero_permis", nullable = false, length = 50)
    private String numeroPermis;

    @OneToMany(mappedBy = "conducteur")
    private List<Vehicule> vehicules = new ArrayList<>();

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

    public List<Vehicule> getVehicules() {
        return vehicules;
    }

    public void ajouterVehicule(Vehicule vehicule) {
        vehicules.add(vehicule);
        vehicule.setConducteur(this);
    }
}
