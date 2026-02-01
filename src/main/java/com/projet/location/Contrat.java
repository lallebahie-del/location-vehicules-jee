package com.projet.location;

import java.time.LocalDate;

public class Contrat {

    private Long id;
    private LocalDate dateDebut;
    private LocalDate dateRetourPrevue;
    private LocalDate dateRetourEffective;
    private double prixJournalier;
    private StatutContrat statut;

    public Contrat() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateRetourPrevue() {
        return dateRetourPrevue;
    }

    public void setDateRetourPrevue(LocalDate dateRetourPrevue) {
        this.dateRetourPrevue = dateRetourPrevue;
    }

    public LocalDate getDateRetourEffective() {
        return dateRetourEffective;
    }

    public void setDateRetourEffective(LocalDate dateRetourEffective) {
        this.dateRetourEffective = dateRetourEffective;
    }

    public double getPrixJournalier() {
        return prixJournalier;
    }

    public void setPrixJournalier(double prixJournalier) {
        this.prixJournalier = prixJournalier;
    }

    public StatutContrat getStatut() {
        return statut;
    }

    public void setStatut(StatutContrat statut) {
        this.statut = statut;
    }
}
