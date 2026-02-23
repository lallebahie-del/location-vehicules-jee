package service;

import model.Vehicle;
import storage.CategoryPricingStorage;
import storage.VehicleStorage;
import storage.ReservationStorage;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VehicleService {

    // Récupérer tous les véhicules
    public static Map<Long, Vehicle> getAllVehicles() {
        return VehicleStorage.getAllVehicles();
    }

    // Récupérer les véhicules disponibles
    public static List<Vehicle> getAvailableVehicles() {
        return VehicleStorage.getAvailableVehicles();
    }

    // Récupérer un véhicule par ID
    public static Vehicle getVehicleById(Long id) {
        return VehicleStorage.getVehicleById(id);
    }

    // Vérifier la disponibilité pour une période donnée
    public static boolean isVehicleAvailableForPeriod(Long vehicleId, LocalDate dateDebut, LocalDate dateFin) {
        Vehicle vehicle = VehicleStorage.getVehicleById(vehicleId);
        if (vehicle == null || !"DISPONIBLE".equals(vehicle.getStatut())) {
            return false;
        }
        return !ReservationStorage.hasConflict(vehicleId, dateDebut, dateFin);
    }

    // Récupérer par catégorie
    public static List<Vehicle> getVehiclesByCategory(String category) {
        return VehicleStorage.getVehiclesByCategory(category);
    }

    // Récupérer par agence
    public static List<Vehicle> getVehiclesByAgency(String agency) {
        return VehicleStorage.getVehiclesByAgency(agency);
    }

    // Mettre à jour le statut d'un véhicule
    public static void updateVehicleStatus(Long vehicleId, String newStatus) {
        Vehicle vehicle = VehicleStorage.getVehicleById(vehicleId);
        if (vehicle != null) {
            vehicle.setStatut(newStatus);
            VehicleStorage.updateVehicle(vehicle);
        }
    }

    // Calculer le prix pour une période (avec options)
    public static double calculatePriceForPeriod(Long vehicleId, LocalDate dateDebut, LocalDate dateFin,
            boolean avecChauffeur,
            boolean optionGPS, boolean optionSiegeBebe,
            boolean optionAssurance) {
        Vehicle vehicle = VehicleStorage.getVehicleById(vehicleId);
        if (vehicle == null)
            return 0;

        long days = ChronoUnit.DAYS.between(dateDebut, dateFin);
        if (days <= 0)
            days = 1;

        double prixBase = vehicle.getTarifJournalier() * days;
        double prixOptions = 0;

        // Options supplémentaires (tarif journalier)
        if (optionGPS)
            prixOptions += 5.0 * days; // 5€/jour
        if (optionSiegeBebe)
            prixOptions += 3.0 * days; // 3€/jour
        if (optionAssurance)
            prixOptions += 15.0 * days; // 15€/jour
        if (avecChauffeur)
            prixBase *= 1.3; // +30% pour chauffeur

        return prixBase + prixOptions;
    }

    // Calculer le prix (version simple - compatibilité)
    public static double calculatePriceForPeriod(Long vehicleId, LocalDate dateDebut, LocalDate dateFin,
            boolean avecChauffeur) {
        return calculatePriceForPeriod(vehicleId, dateDebut, dateFin, avecChauffeur,
                false, false, false);
    }

    // Ajouter un véhicule
    public static Vehicle addVehicle(String marque, String modele, int anneeFabrication,
            String immatriculation, String categorie, int nombrePlaces,
            String typeCarburant, double kilometrage,
            double tarifJournalier, String agence) {
        Vehicle vehicle = new Vehicle(null, marque, modele, anneeFabrication,
                immatriculation, categorie, nombrePlaces,
                typeCarburant, kilometrage, tarifJournalier, agence);
        VehicleStorage.addVehicle(vehicle);
        return vehicle;
    }

    // Modifier un véhicule
    public static String updateVehicle(Long vehicleId, String marque, String modele,
            double tarifJournalier, String agence, String statut) {
        Vehicle vehicle = VehicleStorage.getVehicleById(vehicleId);
        if (vehicle == null) {
            return "Véhicule non trouvé.";
        }
        if (marque != null)
            vehicle.setMarque(marque);
        if (modele != null)
            vehicle.setModele(modele);
        if (tarifJournalier > 0)
            vehicle.setTarifJournalier(tarifJournalier);
        if (agence != null)
            vehicle.setAgence(agence);
        if (statut != null)
            vehicle.setStatut(statut);
        VehicleStorage.updateVehicle(vehicle);
        return null; // Succès
    }

    // Supprimer un véhicule
    public static String deleteVehicle(Long vehicleId) {
        if (!VehicleStorage.deleteVehicle(vehicleId)) {
            return "Véhicule non trouvé.";
        }
        return null; // Succès
    }

    // Mettre un véhicule en maintenance
    public static String setMaintenance(Long vehicleId) {
        Vehicle vehicle = VehicleStorage.getVehicleById(vehicleId);
        if (vehicle == null)
            return "Véhicule non trouvé.";
        if ("EN_LOCATION".equals(vehicle.getStatut())) {
            return "Le véhicule est actuellement en location, impossible de le mettre en maintenance.";
        }
        vehicle.setStatut("EN_MAINTENANCE");
        VehicleStorage.updateVehicle(vehicle);
        return null;
    }

    // Remettre en service après maintenance
    public static String backToService(Long vehicleId) {
        Vehicle vehicle = VehicleStorage.getVehicleById(vehicleId);
        if (vehicle == null)
            return "Véhicule non trouvé.";
        if (!"EN_MAINTENANCE".equals(vehicle.getStatut())) {
            return "Le véhicule n'est pas en maintenance.";
        }
        vehicle.setStatut("DISPONIBLE");
        VehicleStorage.updateVehicle(vehicle);
        return null;
    }

    // Véhicules en maintenance
    public static List<Vehicle> getVehiclesEnMaintenance() {
        return VehicleStorage.getVehiclesEnMaintenance();
    }

    // Catégories disponibles
    public static Set<String> getCategories() {
        return VehicleStorage.getCategories();
    }

    // Historique d'un véhicule (contrats liés)
    public static List<model.RentalContract> getVehicleHistory(Long vehicleId) {
        return storage.ContractStorage.getContractsByVehicle(vehicleId);
    }

    // -------------------------------------------------------
    // Signaler un véhicule comme accidenté
    // -------------------------------------------------------
    public static String setAccidente(Long vehicleId) {
        Vehicle vehicle = VehicleStorage.getVehicleById(vehicleId);
        if (vehicle == null)
            return "Véhicule non trouvé.";
        if ("EN_LOCATION".equals(vehicle.getStatut()))
            return "Le véhicule est actuellement en location, impossible de le marquer comme accidenté.";
        vehicle.setStatut("ACCIDENTE");
        VehicleStorage.updateVehicle(vehicle);
        return null;
    }

    // Remettre un véhicule accidenté en service (après réparation)
    public static String repairVehicle(Long vehicleId) {
        Vehicle vehicle = VehicleStorage.getVehicleById(vehicleId);
        if (vehicle == null)
            return "Véhicule non trouvé.";
        if (!"ACCIDENTE".equals(vehicle.getStatut()))
            return "Le véhicule n'est pas marqué comme accidenté.";
        vehicle.setStatut("DISPONIBLE");
        VehicleStorage.updateVehicle(vehicle);
        return null;
    }

    // -------------------------------------------------------
    // Vérification des contraintes par catégorie (age + permis)
    // -------------------------------------------------------
    public static String checkCategoryConstraints(String category, int clientAge, int licenseYears) {
        int minAge = "LUXE".equalsIgnoreCase(category) ? 25 : 21;
        if (clientAge < minAge) {
            return "Le client doit avoir au moins " + minAge + " ans pour la catégorie " + category + ".";
        }
        if ("SUV".equalsIgnoreCase(category) && licenseYears < 2) {
            return "Le permis doit avoir été obtenu depuis au moins 2 ans pour la catégorie SUV.";
        }
        if ("LUXE".equalsIgnoreCase(category) && licenseYears < 3) {
            return "Le permis doit avoir été obtenu depuis au moins 3 ans pour la catégorie LUXE.";
        }
        return "OK";
    }

    // -------------------------------------------------------
    // Gestion des tarifs par catégorie (admin)
    // -------------------------------------------------------
    public static Map<String, Double> getAllCategoryPricing() {
        return CategoryPricingStorage.getAllPricing();
    }

    public static String setCategoryPrice(String category, double price) {
        if (price <= 0)
            return "Le tarif doit être supérieur à 0.";
        CategoryPricingStorage.setCategoryPrice(category, price);
        return null; // succès
    }

    // -------------------------------------------------------
    // Calcul du kilométrage excédentaire
    // -------------------------------------------------------
    /**
     * Retourne les km excédentaires si une limite est définie et dépassée, sinon 0.
     */
    public static double calculateMileageOverage(Long vehicleId, double kmDepart, double kmRetour) {
        Vehicle vehicle = VehicleStorage.getVehicleById(vehicleId);
        if (vehicle == null)
            return 0.0;
        double limite = vehicle.getLimiteKilometrage();
        if (limite <= 0)
            return 0.0; // illimité
        double kmParcourus = kmRetour - kmDepart;
        return Math.max(0.0, kmParcourus - limite);
    }
}
