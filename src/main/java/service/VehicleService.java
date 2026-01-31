package service;

import model.Vehicle;
import storage.VehicleStorage;
import storage.ReservationStorage;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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
    
    // Vérifier la disponibilité d'un véhicule pour une période
    public static boolean isVehicleAvailableForPeriod(Long vehicleId, LocalDate startDate, LocalDate endDate) {
        // Vérifier si le véhicule existe et est disponible
        Vehicle vehicle = VehicleStorage.getVehicleById(vehicleId);
        if (vehicle == null || !"DISPONIBLE".equals(vehicle.getStatut())) {
            return false;
        }
        
        // Vérifier les réservations existantes
        return !ReservationStorage.isVehicleReservedForPeriod(vehicleId, startDate, endDate);
    }
    
    // Filtrer les véhicules par catégorie
    public static List<Vehicle> getVehiclesByCategory(String category) {
        return VehicleStorage.getVehiclesByCategory(category);
    }
    
    // Filtrer les véhicules par agence
    public static List<Vehicle> getVehiclesByAgency(String agency) {
        return VehicleStorage.getVehiclesByAgency(agency);
    }
    
    // Mettre à jour le statut d'un véhicule
    public static boolean updateVehicleStatus(Long vehicleId, String newStatus) {
        Vehicle vehicle = VehicleStorage.getVehicleById(vehicleId);
        if (vehicle == null) return false;
        
        vehicle.setStatut(newStatus);
        VehicleStorage.updateVehicle(vehicle);
        return true;
    }
    
    // Ajouter un nouveau véhicule
    public static Vehicle addVehicle(String marque, String modele, String immatriculation,
                                     String categorie, double tarifJournalier, 
                                     String agence, int nombrePlaces) {
        
        Vehicle vehicle = new Vehicle();
        vehicle.setMarque(marque);
        vehicle.setModele(modele);
        vehicle.setImmatriculation(immatriculation);
        vehicle.setCategorie(categorie);
        vehicle.setTarifJournalier(tarifJournalier);
        vehicle.setAgence(agence);
        vehicle.setNombrePlaces(nombrePlaces);
        vehicle.setStatut("DISPONIBLE");
        
        VehicleStorage.addVehicle(vehicle);
        return vehicle;
    }
    
    // Calculer le prix pour une période
    public static double calculatePriceForPeriod(Long vehicleId, LocalDate startDate, LocalDate endDate, boolean avecChauffeur) {
        Vehicle vehicle = VehicleStorage.getVehicleById(vehicleId);
        if (vehicle == null) return 0.0;
        
        int days = (int) java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        double basePrice = vehicle.getTarifJournalier() * days;
        
        // Supplément chauffeur: +30%
        if (avecChauffeur) {
            basePrice *= 1.3;
        }
        
        return basePrice;
    }
    
    // Vérifier les contraintes de catégorie
    public static String checkCategoryConstraints(String category, int clientAge, int licenseYears) {
        if (category == null) return "Catégorie non spécifiée";
        
        switch (category.toUpperCase()) {
            case "SUV":
                if (clientAge < 25) return "Âge minimum 25 ans pour les SUV";
                if (licenseYears < 2) return "Permis minimum 2 ans pour les SUV";
                break;
                
            case "LUXE":
                if (clientAge < 25) return "Âge minimum 25 ans pour les véhicules de luxe";
                if (licenseYears < 3) return "Permis minimum 3 ans pour les véhicules de luxe";
                break;
                
            case "ECONOMIQUE":
            case "CONFORT":
                if (clientAge < 21) return "Âge minimum 21 ans pour cette catégorie";
                if (licenseYears < 1) return "Permis minimum 1 an pour cette catégorie";
                break;
        }
        
        return "OK";
    }
}