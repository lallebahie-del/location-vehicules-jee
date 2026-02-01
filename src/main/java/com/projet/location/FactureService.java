package com.projet.location;

import java.time.LocalDate;

public class FactureService {

    private ContaService contaService = new ContaService();

    public Facture genererFacture(Contrat conta) {
        Facture facture = new Facture();
        facture.setDateFacturation(LocalDate.now());

        double penalite = contaService.calculerPenalite(conta);
        double montant = contaService.calculerCout(conta);

        facture.setPenalite(penalite);
        facture.setMontantTotal(montant + penalite);
        facture.setConta(conta);

        return facture;
    }
}