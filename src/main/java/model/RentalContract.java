package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class RentalContract {
    private Long id;
    private Long reservationId;
    private Long clientId;
    private Long vehicleId;
    private Long managerId; // Gestionnaire qui a validé
    private LocalDate dateDebut;
    private LocalDate dateFinPrevue;
    private LocalDate dateFinReelle;
    private double caution;
    private String etatDepart; // Description état véhicule au départ
    private String etatRetour; // Description état véhicule au retour
    private double penalites; // Total des pénalités
    private String statut; // ACTIF, CLOTURE, EN_RETARD
    private LocalDateTime dateCreation;
    private double kilometrageDepart; // Kilométrage au départ
    private double kilometrageRetour; // Kilométrage au retour
    private double niveauCarburantDepart; // Niveau carburant au départ (%)
    private double niveauCarburantRetour; // Niveau carburant au retour (%)
    private boolean documentsVerifies; // Permis et pièce d'identité vérifiés
    private String detailsPenalites; // Détails des pénalités

    // Constructors
    public RentalContract() {
        this.dateCreation = LocalDateTime.now();
        this.statut = "ACTIF";
        this.caution = 500.0;
    }

    public RentalContract(Long id, Long reservationId, Long clientId,
            Long vehicleId, Long managerId,
            LocalDate dateDebut, LocalDate dateFinPrevue) {
        this.id = id;
        this.reservationId = reservationId;
        this.clientId = clientId;
        this.vehicleId = vehicleId;
        this.managerId = managerId;
        this.dateDebut = dateDebut;
        this.dateFinPrevue = dateFinPrevue;
        this.caution = 500.0;
        this.statut = "ACTIF";
        this.dateCreation = LocalDateTime.now();
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
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

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFinPrevue() {
        return dateFinPrevue;
    }

    public void setDateFinPrevue(LocalDate dateFinPrevue) {
        this.dateFinPrevue = dateFinPrevue;
    }

    public LocalDate getDateFinReelle() {
        return dateFinReelle;
    }

    public void setDateFinReelle(LocalDate dateFinReelle) {
        this.dateFinReelle = dateFinReelle;
    }

    public double getCaution() {
        return caution;
    }

    public void setCaution(double caution) {
        this.caution = caution;
    }

    public String getEtatDepart() {
        return etatDepart;
    }

    public void setEtatDepart(String etatDepart) {
        this.etatDepart = etatDepart;
    }

    public String getEtatRetour() {
        return etatRetour;
    }

    public void setEtatRetour(String etatRetour) {
        this.etatRetour = etatRetour;
    }

    public double getPenalites() {
        return penalites;
    }

    public void setPenalites(double penalites) {
        this.penalites = penalites;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public double getKilometrageDepart() {
        return kilometrageDepart;
    }

    public void setKilometrageDepart(double kilometrageDepart) {
        this.kilometrageDepart = kilometrageDepart;
    }

    public double getKilometrageRetour() {
        return kilometrageRetour;
    }

    public void setKilometrageRetour(double kilometrageRetour) {
        this.kilometrageRetour = kilometrageRetour;
    }

    public double getNiveauCarburantDepart() {
        return niveauCarburantDepart;
    }

    public void setNiveauCarburantDepart(double niveauCarburantDepart) {
        this.niveauCarburantDepart = niveauCarburantDepart;
    }

    public double getNiveauCarburantRetour() {
        return niveauCarburantRetour;
    }

    public void setNiveauCarburantRetour(double niveauCarburantRetour) {
        this.niveauCarburantRetour = niveauCarburantRetour;
    }

    public boolean isDocumentsVerifies() {
        return documentsVerifies;
    }

    public void setDocumentsVerifies(boolean documentsVerifies) {
        this.documentsVerifies = documentsVerifies;
    }

    public String getDetailsPenalites() {
        return detailsPenalites;
    }

    public void setDetailsPenalites(String detailsPenalites) {
        this.detailsPenalites = detailsPenalites;
    }
}