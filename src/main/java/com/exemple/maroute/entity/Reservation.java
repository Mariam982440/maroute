package com.exemple.maroute.entity;

import com.exemple.maroute.enums.StatutReservation;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "passager_id", nullable = false)
    private Passager passager;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trajet_id", nullable = false)
    private Trajet trajet;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "point_montee_id", nullable = false)
    private PointArret pointMontee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "point_descente_id", nullable = false)
    private PointArret pointDescente;

    @Column(name = "nombre_places", nullable = false)
    private int nombrePlaces;

    @Column(name = "montant_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantTotal;

    @Column(name = "date_reservation", nullable = false)
    private LocalDateTime dateReservation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutReservation statut;

    public Reservation() {
    }

    public Reservation(Passager passager, Trajet trajet, PointArret pointMontee,
                       PointArret pointDescente, int nombrePlaces, BigDecimal montantTotal,
                       LocalDateTime dateReservation, StatutReservation statut) {
        this.passager = passager;
        this.trajet = trajet;
        this.pointMontee = pointMontee;
        this.pointDescente = pointDescente;
        this.nombrePlaces = nombrePlaces;
        this.montantTotal = montantTotal;
        this.dateReservation = dateReservation;
        this.statut = statut;
    }

    public Long getId() {
        return id;
    }

    public Passager getPassager() {
        return passager;
    }

    public Trajet getTrajet() {
        return trajet;
    }

    public PointArret getPointMontee() {
        return pointMontee;
    }

    public PointArret getPointDescente() {
        return pointDescente;
    }

    public int getNombrePlaces() {
        return nombrePlaces;
    }

    public BigDecimal getMontantTotal() {
        return montantTotal;
    }

    public LocalDateTime getDateReservation() {
        return dateReservation;
    }

    public StatutReservation getStatut() {
        return statut;
    }

    public void setStatut(StatutReservation statut) {
        this.statut = statut;
    }
}
