package storage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import model.Vehicle;
import java.util.List;
import java.util.ArrayList;

public class VehicleStorage {

    private static final Map<Long, Vehicle> vehicles = new HashMap<>();
    private static final Set<String> categories = new HashSet<>();
    private static long idCounter = 1L;

    // Initialisation avec des véhicules de test
    static {
        // Véhicule Économique
        Vehicle v1 = new Vehicle(idCounter++, "Renault", "Clio", 2022,
                "AB-123-CD", "ECONOMIQUE", 5, "ESSENCE", 15000, 45.0, "Agence Paris");
        vehicles.put(v1.getId(), v1);
        categories.add(v1.getCategorie());

        // Véhicule Compact
        Vehicle v2 = new Vehicle(idCounter++, "Peugeot", "308", 2023,
                "EF-456-GH", "COMPACT", 5, "DIESEL", 8000, 55.0, "Agence Paris");
        vehicles.put(v2.getId(), v2);
        categories.add(v2.getCategorie());

        // SUV
        Vehicle v3 = new Vehicle(idCounter++, "Toyota", "RAV4", 2023,
                "IJ-789-KL", "SUV", 5, "HYBRIDE", 5000, 90.0, "Agence Lyon");
        vehicles.put(v3.getId(), v3);
        categories.add(v3.getCategorie());

        // Véhicule Luxe
        Vehicle v4 = new Vehicle(idCounter++, "Mercedes", "Classe C", 2024,
                "MN-012-OP", "LUXE", 5, "ESSENCE", 2000, 150.0, "Agence Paris");
        vehicles.put(v4.getId(), v4);
        categories.add(v4.getCategorie());

        // Véhicule Utilitaire
        Vehicle v5 = new Vehicle(idCounter++, "Renault", "Master", 2021,
                "QR-345-ST", "UTILITAIRE", 3, "DIESEL", 45000, 70.0, "Agence Lyon");
        vehicles.put(v5.getId(), v5);
        categories.add(v5.getCategorie());

        // Véhicule en maintenance
        Vehicle v6 = new Vehicle(idCounter++, "Volkswagen", "Golf", 2022,
                "UV-678-WX", "COMPACT", 5, "ESSENCE", 30000, 50.0, "Agence Lyon");
        v6.setStatut("EN_MAINTENANCE");
        vehicles.put(v6.getId(), v6);
        categories.add(v6.getCategorie());
    }

    // Méthodes CRUD
    public static Map<Long, Vehicle> getAllVehicles() {
        return new HashMap<>(vehicles);
    }

    public static List<Vehicle> getAvailableVehicles() {
        List<Vehicle> available = new ArrayList<>();
        for (Vehicle v : vehicles.values()) {
            if ("DISPONIBLE".equals(v.getStatut())) {
                available.add(v);
            }
        }
        return available;
    }

    public static Vehicle getVehicleById(Long id) {
        return vehicles.get(id);
    }

    public static void addVehicle(Vehicle vehicle) {
        if (vehicle.getId() == null) {
            vehicle.setId(idCounter++);
        }
        vehicles.put(vehicle.getId(), vehicle);
        if (vehicle.getCategorie() != null) {
            categories.add(vehicle.getCategorie());
        }
    }

    public static void updateVehicle(Vehicle vehicle) {
        vehicles.put(vehicle.getId(), vehicle);
    }

    public static boolean deleteVehicle(Long id) {
        return vehicles.remove(id) != null;
    }

    public static List<Vehicle> getVehiclesByCategory(String category) {
        List<Vehicle> result = new ArrayList<>();
        for (Vehicle v : vehicles.values()) {
            if (category.equals(v.getCategorie()) && "DISPONIBLE".equals(v.getStatut())) {
                result.add(v);
            }
        }
        return result;
    }

    public static List<Vehicle> getVehiclesByAgency(String agency) {
        List<Vehicle> result = new ArrayList<>();
        for (Vehicle v : vehicles.values()) {
            if (agency.equals(v.getAgence())) {
                result.add(v);
            }
        }
        return result;
    }

    public static boolean isVehicleAvailable(Long vehicleId) {
        Vehicle v = vehicles.get(vehicleId);
        return v != null && "DISPONIBLE".equals(v.getStatut());
    }

    public static List<Vehicle> getVehiclesEnMaintenance() {
        List<Vehicle> result = new ArrayList<>();
        for (Vehicle v : vehicles.values()) {
            if ("EN_MAINTENANCE".equals(v.getStatut())) {
                result.add(v);
            }
        }
        return result;
    }

    public static long getNextId() {
        return idCounter;
    }

    // Récupérer les catégories uniques (utilisation de Set)
    public static Set<String> getCategories() {
        return new HashSet<>(categories);
    }
}