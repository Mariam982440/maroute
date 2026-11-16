package com.exemple.maroute.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(
        name = "vehicules",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_vehicule_immatriculation",
                        columnNames = "immatriculation"
                )
        }
)
public class Vehicule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "la marque est obligatoire")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String marque;

    @NotBlank(message = "le modèle est obligatoire")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String modele;

    @NotBlank(message = "l'immatriculation est obligatoire")
    @Size(max = 30)
    @Column(nullable = false, length = 30)
    private String immatriculation;

    @NotBlank(message = "la couleur est obligatoire")
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String couleur;

    @NotNull(message = "la capacité est obligatoire")
    @Min(value = 1, message = "la capacité minimale est de 1 place")
    @Max(value = 8, message = "la capacité maximale est de 8 places")
    @Column(nullable = false)
    private Integer capacite;


    public Vehicule() {
    }

    public Vehicule(String marque, String modele, String immatriculation,
                    String couleur, Integer capacite) {
        this.marque = marque;
        this.modele = modele;
        this.immatriculation = immatriculation;
        this.couleur = couleur;
        this.capacite = capacite;
    }

    public Long getId() {
        return id;
    }

    public String getMarque() {
        return marque;
    }

    public void setMarque(String marque) {
        this.marque = marque;
    }

    public String getModele() {
        return modele;
    }

    public void setModele(String modele) {
        this.modele = modele;
    }

    public String getImmatriculation() {
        return immatriculation;
    }

    public void setImmatriculation(String immatriculation) {
        this.immatriculation = immatriculation;
    }

    public String getCouleur() {
        return couleur;
    }

    public void setCouleur(String couleur) {
        this.couleur = couleur;
    }

    public Integer getCapacite() {
        return capacite;
    }

    public void setCapacite(Integer capacite) {
        this.capacite = capacite;
    }
}


