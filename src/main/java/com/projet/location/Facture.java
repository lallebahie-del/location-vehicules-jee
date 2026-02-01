package com.projet.location;

import java.time.LocalDate;

public class Facture {

    private Long id;
    private LocalDate dateFacturation;
    private double montantTotal;
    private double penalite;
    private Contrat conta;

    public Facture() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDateFacturation() {
        return dateFacturation;
    }

    public void setDateFacturation(LocalDate dateFacturation) {
        this.dateFacturation = dateFacturation;
    }

    public double getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(double montantTotal) {
        this.montantTotal = montantTotal;
    }

    public double getPenalite() {
        return penalite;
    }

    public void setPenalite(double penalite) {
        this.penalite = penalite;
    }

    public Contrat getConta() {
        return conta;
    }

    public void setConta(Contrat conta) {
        this.conta = conta;
    }
}