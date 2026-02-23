package service;

import model.RentalContract;
import model.Reservation;
import model.Vehicle;
import storage.ContractStorage;
import storage.ReservationStorage;
import storage.VehicleStorage;
import storage.InvoiceStorage;
import model.Invoice;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ContractService {

    // Créer un contrat à partir d'une réservation confirmée
    public static RentalContract createContractFromReservation(Long reservationId, Long managerId) {
        // 1. Vérifier la réservation
        Reservation reservation = ReservationStorage.getReservationById(reservationId);
        if (reservation == null || !"CONFIRMEE".equals(reservation.getStatut())) {
            throw new IllegalArgumentException("Réservation non confirmée ou introuvable");
        }

        // 2. Vérifier qu'aucun contrat n'existe déjà
        if (ContractStorage.getContractByReservationId(reservationId) != null) {
            throw new IllegalArgumentException("Un contrat existe déjà pour cette réservation");
        }

        // 3. Vérifier la date (contrat ne peut être créé que le jour du début)
        LocalDate today = LocalDate.now();
        if (!today.equals(reservation.getDateDebut())) {
            throw new IllegalArgumentException("Le contrat ne peut être créé que le jour du début de location");
        }

        // 4. Créer le contrat
        RentalContract contract = new RentalContract();
        contract.setReservationId(reservationId);
        contract.setClientId(reservation.getClientId());
        contract.setVehicleId(reservation.getVehicleId());
        contract.setManagerId(managerId);
        contract.setDateDebut(reservation.getDateDebut());
        contract.setDateFinPrevue(reservation.getDateFin());
        contract.setCaution(500.0); // Caution standard
        contract.setStatut("ACTIF");

        ContractStorage.addContract(contract);

        // 5. Mettre à jour le statut du véhicule
        VehicleService.updateVehicleStatus(reservation.getVehicleId(), "EN_LOCATION");

        return contract;
    }

    // Clôturer un contrat (retour du véhicule)
    public static RentalContract closeContract(Long contractId, String etatRetour,
            double niveauCarburantRetour, // 0.0 à 1.0 (1.0 = plein)
            boolean dommages,
            double kilometrageRetour) {

        RentalContract contract = ContractStorage.getContractById(contractId);
        if (contract == null || !"ACTIF".equals(contract.getStatut())) {
            throw new IllegalArgumentException("Contrat introuvable ou déjà clôturé");
        }

        LocalDate today = LocalDate.now();
        contract.setDateFinReelle(today);
        contract.setEtatRetour(etatRetour);
        contract.setNiveauCarburantRetour(niveauCarburantRetour);
        contract.setKilometrageRetour(kilometrageRetour);
        contract.setStatut("CLOTURE");

        // Calculer les pénalités
        double penalites = calculatePenalties(contract, today, niveauCarburantRetour, dommages, kilometrageRetour);
        contract.setPenalites(penalites);

        ContractStorage.updateContract(contract);

        // Libérer le véhicule
        VehicleService.updateVehicleStatus(contract.getVehicleId(), "DISPONIBLE");

        // Créer la facture
        createInvoiceForContract(contract, penalites);

        return contract;
    }

    // Surcharge pour compatibilité avec l'ancien appel (sans km retour)
    public static RentalContract closeContract(Long contractId, String etatRetour,
            double niveauCarburant, boolean dommages) {
        return closeContract(contractId, etatRetour, niveauCarburant, dommages, 0.0);
    }

    // Calculer les pénalités
    private static double calculatePenalties(RentalContract contract, LocalDate returnDate,
            double niveauCarburantRetour, boolean dommages,
            double kilometrageRetour) {
        double totalPenalties = 0.0;
        StringBuilder details = new StringBuilder();

        // 1. Pénalité de retard (20% du tarif journalier par heure de retard)
        if (returnDate.isAfter(contract.getDateFinPrevue())) {
            long heuresRetard = ChronoUnit.HOURS.between(
                    contract.getDateFinPrevue().atStartOfDay(),
                    returnDate.atStartOfDay());

            Vehicle vehicle = VehicleStorage.getVehicleById(contract.getVehicleId());
            if (vehicle != null && heuresRetard > 0) {
                double penaliteRetard = (vehicle.getTarifJournalier() * 0.20) * heuresRetard;
                totalPenalties += penaliteRetard;
                details.append("Retard ").append(heuresRetard).append("h: +")
                        .append(String.format("%.2f", penaliteRetard)).append("€; ");
            }
        }

        // 2. Pénalité carburant — niveau manquant x prix litre x majoration 30%
        // Hypothèses : réservoir moyen = 50 litres, prix de référence = 1.80 EUR/L
        double niveauDepart = contract.getNiveauCarburantDepart(); // peut être 0 si non renseigné
        // Si le niveau de départ n'a pas été renseigné, on utilise 1.0 (plein) comme
        // référence
        double niveauRef = (niveauDepart > 0) ? niveauDepart : 1.0;
        if (niveauCarburantRetour < niveauRef) {
            double niveauManquant = niveauRef - niveauCarburantRetour; // ex: 0.25 = 1/4 de réservoir
            double litresManquants = niveauManquant * 50.0; // en litres
            double coutCarburant = litresManquants * 1.80 * 1.30; // prix + majoration 30%
            totalPenalties += coutCarburant;
            details.append("Carburant (")
                    .append(String.format("%.0f", litresManquants))
                    .append("L manquants): +")
                    .append(String.format("%.2f", coutCarburant))
                    .append("€; ");
        }

        // 3. Pénalité kilométrage excédentaire (si limite définie)
        if (kilometrageRetour > 0 && contract.getKilometrageDepart() > 0) {
            double kmOverage = VehicleService.calculateMileageOverage(
                    contract.getVehicleId(),
                    contract.getKilometrageDepart(),
                    kilometrageRetour);
            if (kmOverage > 0) {
                Vehicle vehicle = VehicleStorage.getVehicleById(contract.getVehicleId());
                // Pénalité = tarif journalier / 200 par km excédentaire (environ 0.25€/km)
                double tarifJournalier = (vehicle != null) ? vehicle.getTarifJournalier() : 50.0;
                double penaliteKm = kmOverage * (tarifJournalier / 200.0);
                totalPenalties += penaliteKm;
                details.append("Kilométrage excédentaire (").append(String.format("%.0f", kmOverage)).append(" km): +")
                        .append(String.format("%.2f", penaliteKm)).append("€; ");
            }
        }

        // 4. Pénalité dommages (forfait minimum 100€)
        if (dommages) {
            totalPenalties += 100.0;
            details.append("Dommages: +100.00€; ");
        }

        contract.setDetailsPenalites(details.length() > 0 ? details.toString() : "Aucune pénalité");
        return totalPenalties;
    }

    // Surcharge pour compatibilité (appels internes sans km)
    private static double calculatePenalties(RentalContract contract, LocalDate returnDate,
            double niveauCarburant, boolean dommages) {
        return calculatePenalties(contract, returnDate, niveauCarburant, dommages, 0.0);
    }

    // Créer une facture pour un contrat
    private static void createInvoiceForContract(RentalContract contract, double penalites) {
        // Récupérer la réservation pour connaître le montant de location
        Reservation reservation = ReservationStorage.getReservationById(contract.getReservationId());
        if (reservation == null)
            return;

        Invoice invoice = new Invoice();
        invoice.setContractId(contract.getId());
        invoice.setClientId(contract.getClientId());
        invoice.setMontantLocation(reservation.getMontantTotal());
        invoice.setMontantPenalites(penalites);

        // Ajouter des détails sur les pénalités
        StringBuilder details = new StringBuilder();
        if (penalites > 0) {
            details.append("Pénalités appliquées: ");
            if (contract.getDateFinReelle().isAfter(contract.getDateFinPrevue())) {
                details.append("Retard, ");
            }
            details.append("Total: ").append(penalites).append("€");
        } else {
            details.append("Aucune pénalité");
        }
        invoice.setDetailsPenalites(details.toString());

        InvoiceStorage.addInvoice(invoice);
    }

    // Vérifier les contrats en retard
    public static List<RentalContract> checkOverdueContracts() {
        return ContractStorage.getOverdueContracts();
    }

    // Récupérer les contrats actifs d'un client
    public static List<RentalContract> getClientActiveContracts(Long clientId) {
        List<RentalContract> allContracts = ContractStorage.getContractsByClient(clientId);
        List<RentalContract> active = new java.util.ArrayList<>();

        for (RentalContract c : allContracts) {
            if ("ACTIF".equals(c.getStatut())) {
                active.add(c);
            }
        }

        return active;
    }

    // Calculer le coût total d'un contrat (location + pénalités)
    public static double calculateTotalCost(Long contractId) {
        RentalContract contract = ContractStorage.getContractById(contractId);
        if (contract == null)
            return 0.0;

        Reservation reservation = ReservationStorage.getReservationById(contract.getReservationId());
        double locationCost = (reservation != null) ? reservation.getMontantTotal() : 0.0;

        return locationCost + contract.getPenalites();
    }

    // Remboursement de caution
    public static double calculateCautionRefund(Long contractId) {
        RentalContract contract = ContractStorage.getContractById(contractId);
        if (contract == null || !"CLOTURE".equals(contract.getStatut())) {
            return 0.0;
        }

        double caution = contract.getCaution();
        double penalites = contract.getPenalites();

        // La caution couvre les pénalités jusqu'à son montant
        double refund = caution - penalites;
        return Math.max(refund, 0.0); // Pas de remboursement négatif
    }
}