package model;

import java.time.LocalDate;

public class Reservation {
    private Long id;
    private Long clientId;
    private Long vehicleId;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private double montantTotal;
    private String statut; // EN_ATTENTE, CONFIRMEE, ANNULEE, TERMINEE
    private LocalDate dateReservation;
    private boolean avecChauffeur;

    // Constructors
    public Reservation() {
        this.dateReservation = LocalDate.now();
        this.statut = "EN_ATTENTE";
    }

    public Reservation(Long id, Long clientId, Long vehicleId, 
                       LocalDate dateDebut, LocalDate dateFin, 
                       double montantTotal, boolean avecChauffeur) {
        this.id = id;
        this.clientId = clientId;
        this.vehicleId = vehicleId;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.montantTotal = montantTotal;
        this.statut = "EN_ATTENTE";
        this.dateReservation = LocalDate.now();
        this.avecChauffeur = avecChauffeur;
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public double getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(double montantTotal) {
        this.montantTotal = montantTotal;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public LocalDate getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(LocalDate dateReservation) {
        this.dateReservation = dateReservation;
    }

    public boolean isAvecChauffeur() {
        return avecChauffeur;
    }

    public void setAvecChauffeur(boolean avecChauffeur) {
        this.avecChauffeur = avecChauffeur;
    }
}