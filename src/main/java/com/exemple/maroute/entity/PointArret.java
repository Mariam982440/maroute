package com.exemple.maroute.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "points_arret",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_point_trajet_ordre",
                        columnNames = {"trajet_id", "ordre"}
                ),
                @UniqueConstraint(
                        name = "uk_point_trajet_ville",
                        columnNames = {"trajet_id", "ville"}
                )
        }
)
public class PointArret {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trajet_id", nullable = false)
    private Trajet trajet;

    @Column(nullable = false, length = 100)
    private String ville;

    @Column(nullable = false)
    private int ordre;

    @Column(name = "heure_passage", nullable = false)
    private LocalDateTime heurePassage;

    @Column(name = "prix_depuis_depart", nullable = false, precision = 10, scale = 2)
    private BigDecimal prixDepuisDepart;

    public PointArret() {
    }

    public PointArret(String ville, int ordre, LocalDateTime heurePassage,
                      BigDecimal prixDepuisDepart) {
        this.ville = ville;
        this.ordre = ordre;
        this.heurePassage = heurePassage;
        this.prixDepuisDepart = prixDepuisDepart;
    }

    public Long getId() {
        return id;
    }

    public Trajet getTrajet() {
        return trajet;
    }

    public void setTrajet(Trajet trajet) {
        this.trajet = trajet;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public int getOrdre() {
        return ordre;
    }

    public void setOrdre(int ordre) {
        this.ordre = ordre;
    }

    public LocalDateTime getHeurePassage() {
        return heurePassage;
    }

    public void setHeurePassage(LocalDateTime heurePassage) {
        this.heurePassage = heurePassage;
    }

    public BigDecimal getPrixDepuisDepart() {
        return prixDepuisDepart;
    }

    public void setPrixDepuisDepart(BigDecimal prixDepuisDepart) {
        this.prixDepuisDepart = prixDepuisDepart;
    }
}
