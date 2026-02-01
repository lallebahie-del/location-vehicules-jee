package com.projet.location;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ContaService {

    public void departLocation(Contrat conta) {
        conta.setDateDebut(LocalDate.now());
        conta.setStatut(StatutContrat.EN_COURS);
    }

    public void retourLocation(Contrat conta) {
        conta.setDateRetourEffective(LocalDate.now());
        conta.setStatut(StatutContrat.TERMINE);
    }

    public double calculerCout(Contrat conta) {
        long jours = ChronoUnit.DAYS.between(
                conta.getDateDebut(),
                conta.getDateRetourEffective()
        );
        return jours * conta.getPrixJournalier();
    }

    public double calculerPenalite(Contrat contrat) {
        if (contrat.getDateRetourEffective().isAfter(contrat.getDateRetourPrevue())) {
            long retard = ChronoUnit.DAYS.between(
                    contrat.getDateRetourPrevue(),
                    contrat.getDateRetourEffective()
            );
            return retard * 50;
        }
        return 0;
    }
}