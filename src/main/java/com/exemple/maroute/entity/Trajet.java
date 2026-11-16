package com.exemple.maroute.entity;


import com.exemple.maroute.enums.StatutTrajet;


import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trajets")
public class Trajet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "le conducteur est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conducteur_id", nullable = false)
    private Conducteur conducteur;

    @NotNull(message = "le véhicule est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicule_id", nullable = false)
    private Vehicule vehicule;

    @NotBlank(message = "la ville de départ est obligatoire")
    @Size(max = 100)
    @Column(name = "ville_depart", nullable = false, length = 100)
    private String villeDepart;

    @NotBlank(message = "la ville d'arrivée est obligatoire")
    @Size(max = 100)
    @Column(name = "ville_arrivee", nullable = false, length = 100)
    private String villeArrivee;

    @NotNull(message = "la date de départ est obligatoire")
    @Column(name = "date_depart", nullable = false)
    private LocalDateTime dateDepart;

    @NotNull(message = "le nombre de places proposées est obligatoire")
    @Min(value = 1, message = "il faut proposer au moins une place")
    @Column(name = "places_proposees", nullable = false)
    private Integer placesProposees;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutTrajet statut = StatutTrajet.PUBLIE;

    @OneToMany(
            mappedBy = "trajet",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("ordre ASC")
    private List<PointArret> pointsArret = new ArrayList<>();

    public Trajet() {
    }

    public Trajet(Conducteur conducteur, Vehicule vehicule,
                  String villeDepart, String villeArrivee,
                  LocalDateTime dateDepart, Integer placesProposees) {
        this.conducteur = conducteur;
        this.vehicule = vehicule;
        this.villeDepart = villeDepart;
        this.villeArrivee = villeArrivee;
        this.dateDepart = dateDepart;
        this.placesProposees = placesProposees;
        this.statut = StatutTrajet.PUBLIE;
    }

    public Long getId() {
        return id;
    }

    public Conducteur getConducteur() {
        return conducteur;
    }

    public void setConducteur(Conducteur conducteur) {
        this.conducteur = conducteur;
    }

    public Vehicule getVehicule() {
        return vehicule;
    }

    public void setVehicule(Vehicule vehicule) {
        this.vehicule = vehicule;
    }

    public String getVilleDepart() {
        return villeDepart;
    }

    public void setVilleDepart(String villeDepart) {
        this.villeDepart = villeDepart;
    }

    public String getVilleArrivee() {
        return villeArrivee;
    }

    public void setVilleArrivee(String villeArrivee) {
        this.villeArrivee = villeArrivee;
    }

    public LocalDateTime getDateDepart() {
        return dateDepart;
    }

    public void setDateDepart(LocalDateTime dateDepart) {
        this.dateDepart = dateDepart;
    }

    public Integer getPlacesProposees() {
        return placesProposees;
    }

    public void setPlacesProposees(Integer placesProposees) {
        this.placesProposees = placesProposees;
    }

    public StatutTrajet getStatut() {
        return statut;
    }

    public void setStatut(StatutTrajet statut) {
        this.statut = statut;
    }

    public List<PointArret> getPointsArret() {
        return pointsArret;
    }

    public void setPointsArret(List<PointArret> pointsArret) {
        this.pointsArret = pointsArret;
    }

//    public void ajouterPointArret(PointArret pointArret) {
//        pointsArret.add(pointArret);
//        pointArret.setTrajet(this);
//    }
}
