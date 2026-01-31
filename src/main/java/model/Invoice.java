package model;

import java.time.LocalDate;

public class Invoice {
    private Long id;
    private Long contractId;
    private Long clientId;
    private LocalDate dateEmission;
    private LocalDate datePaiement;
    private double montantLocation;
    private double montantPenalites;
    private double montantTotal;
    private String statutPaiement; // EN_ATTENTE, PAYEE, EN_RETARD
    private String modePaiement; // CARTE, ESPECES, VIREMENT
    private String detailsPenalites; // Détails des pénalités appliquées

    // Constructors
    public Invoice() {
        this.dateEmission = LocalDate.now();
        this.statutPaiement = "EN_ATTENTE";
    }

    public Invoice(Long id, Long contractId, Long clientId, 
                   double montantLocation, double montantPenalites) {
        this.id = id;
        this.contractId = contractId;
        this.clientId = clientId;
        this.dateEmission = LocalDate.now();
        this.montantLocation = montantLocation;
        this.montantPenalites = montantPenalites;
        this.montantTotal = montantLocation + montantPenalites;
        this.statutPaiement = "EN_ATTENTE";
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public LocalDate getDateEmission() {
        return dateEmission;
    }

    public void setDateEmission(LocalDate dateEmission) {
        this.dateEmission = dateEmission;
    }

    public LocalDate getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(LocalDate datePaiement) {
        this.datePaiement = datePaiement;
    }

    public double getMontantLocation() {
        return montantLocation;
    }

    public void setMontantLocation(double montantLocation) {
        this.montantLocation = montantLocation;
        this.montantTotal = this.montantLocation + this.montantPenalites;
    }

    public double getMontantPenalites() {
        return montantPenalites;
    }

    public void setMontantPenalites(double montantPenalites) {
        this.montantPenalites = montantPenalites;
        this.montantTotal = this.montantLocation + this.montantPenalites;
    }

    public double getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(double montantTotal) {
        this.montantTotal = montantTotal;
    }

    public String getStatutPaiement() {
        return statutPaiement;
    }

    public void setStatutPaiement(String statutPaiement) {
        this.statutPaiement = statutPaiement;
    }

    public String getModePaiement() {
        return modePaiement;
    }

    public void setModePaiement(String modePaiement) {
        this.modePaiement = modePaiement;
    }

    public String getDetailsPenalites() {
        return detailsPenalites;
    }

    public void setDetailsPenalites(String detailsPenalites) {
        this.detailsPenalites = detailsPenalites;
    }
}