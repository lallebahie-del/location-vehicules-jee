package service;

import model.Vehicle;
import storage.VehicleStorage;
import storage.ReservationStorage;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

public class VehicleService {

    // =========================
    // LISTE DES VÉHICULES
    // =========================
    public static List<Vehicle> getAllVehicles() {
        return VehicleStorage.getAllVehicles()
                .values()
                .stream()
                .collect(Collectors.toList());
    }

    public static Vehicle getVehicleById(Long id) {
        return VehicleStorage.getVehicleById(id);
    }

    // =========================
    // DISPONIBILITÉ
    // =========================
    public static boolean isVehicleAvailableForPeriod(
            Long vehicleId,
            LocalDate startDate,
            LocalDate endDate) {

        Vehicle vehicle = VehicleStorage.getVehicleById(vehicleId);
        if (vehicle == null || !"DISPONIBLE".equals(vehicle.getStatut())) {
            return false;
        }

        return !ReservationStorage
                .isVehicleReservedForPeriod(vehicleId, startDate, endDate);
    }

    // =========================
    // STATUT
    // =========================
    public static boolean updateVehicleStatus(Long vehicleId, String newStatus) {
        Vehicle vehicle = VehicleStorage.getVehicleById(vehicleId);
        if (vehicle == null) return false;

        vehicle.setStatut(newStatus);
        VehicleStorage.updateVehicle(vehicle);
        return true;
    }

    // =========================
    // PRIX
    // =========================
    public static double calculatePriceForPeriod(
            Long vehicleId,
            LocalDate startDate,
            LocalDate endDate,
            boolean avecChauffeur) {

        Vehicle vehicle = VehicleStorage.getVehicleById(vehicleId);
        if (vehicle == null) return 0;

        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        double total = vehicle.getTarifJournalier() * days;

        // Chauffeur +30 %
        if (avecChauffeur) {
            total *= 1.3;
        }

        return total;
    }

    // =========================
    // CONTRAINTES CATÉGORIES
    // =========================
    public static String checkCategoryConstraints(
            String categorie,
            int clientAge,
            int licenseYears) {

        if (categorie == null) {
            return "Catégorie non définie";
        }

        switch (categorie.toUpperCase()) {

            case "ECONOMIQUE":
            case "CONFORT":
                if (clientAge < 21) return "Âge minimum 21 ans requis";
                if (licenseYears < 1) return "Permis minimum 1 an requis";
                break;

            case "SUV":
                if (clientAge < 25) return "Âge minimum 25 ans requis pour SUV";
                if (licenseYears < 2) return "Permis minimum 2 ans requis pour SUV";
                break;

            case "LUXE":
                if (clientAge < 25) return "Âge minimum 25 ans requis pour véhicule de luxe";
                if (licenseYears < 3) return "Permis minimum 3 ans requis pour véhicule de luxe";
                break;

            default:
                return "Catégorie inconnue";
        }

        return "OK";
    }
}
