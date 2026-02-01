package com.projet.location;

public class TestConta {

    public static void main(String[] args) {

        // 1️⃣ Créer un contratt
        Contrat conta = new Contrat();
        conta.setPrixJournalier(100);
        conta.setDateRetourPrevue(java.time.LocalDate.now().plusDays(3));

        // 2️⃣ Début de location
        ContaService contaService = new ContaService();
        contaService.departLocation(conta);

        System.out.println("Début de location : " + conta.getDateDebut());
        System.out.println("Statut : " + conta.getStatut());

        // 3️⃣ Retour du véhicule
        contaService.retourLocation(conta);
        System.out.println("Date retour effective : " + conta.getDateRetourEffective());
        System.out.println("Statut après retour : " + conta.getStatut());

        // 4️⃣ Calcul coût et pénalité
        double cout = contaService.calculerCout(conta);
        double penalite = contaService.calculerPenalite(conta);
        System.out.println("Coût : " + cout);
        System.out.println("Pénalité : " + penalite);

        // 5️⃣ Génération facture
        FactureService factureService = new FactureService();
        Facture facture = factureService.genererFacture(conta);

        System.out.println("Facture générée :");
        System.out.println("Montant total : " + facture.getMontantTotal());
        System.out.println("Pénalité : " + facture.getPenalite());
        System.out.println("Date facture : " + facture.getDateFacturation());
    }
}