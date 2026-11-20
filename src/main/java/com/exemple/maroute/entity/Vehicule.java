package com.exemple.maroute.entity;


import jakarta.persistence.*;


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

    @Column(nullable = false, length = 100)
    private String marque;

    @Column(nullable = false, length = 100)
    private String modele;

    @Column(nullable = false, length = 30)
    private String immatriculation;

    @Column(nullable = false, length = 50)
    private String couleur;

    @Column(nullable = false)
    private Integer capacite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conducteur_id", nullable = false)
    private Conducteur conducteur;

    public Vehicule() {
    }

    public Vehicule(String marque, String modele, String immatriculation,
                    String couleur, Integer capacite, Conducteur conducteur) {
        this.marque = marque;
        this.modele = modele;
        this.immatriculation = immatriculation;
        this.couleur = couleur;
        this.capacite = capacite;
        this.conducteur = conducteur;
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

    public Conducteur getConducteur() {
        return conducteur;
    }

    public void setConducteur(Conducteur conducteur) {
        this.conducteur = conducteur;
    }
}


