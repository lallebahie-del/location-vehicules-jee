package storage;

import java.util.HashMap;
import java.util.Map;
import model.Vehicle;
import java.util.List;
import java.util.ArrayList;

public class VehicleStorage {
    
    private static final Map<Long, Vehicle> vehicles = new HashMap<>();
    private static long idCounter = 1L;
    
    // Initialisation avec des véhicules de test
    static {
        // Véhicule Économique
        Vehicle v1 = new Vehicle(idCounter++, "Renault", "Clio", "AB-123-CD", 
                                 "ECONOMIQUE", 45.0, "Agence Paris", 5);
        vehicles.put(v1.getId(), v1);
        
        // Véhicule Confort
        Vehicle v2 = new Vehicle(idCounter++, "Peugeot", "308", "EF-456-GH", 
                                 "CONFORT", 65.0, "Agence Paris", 5);
        vehicles.put(v2.getId(), v2);
        
        // SUV
        Vehicle v3 = new Vehicle(idCounter++, "Toyota", "RAV4", "IJ-789-KL", 
                                 "SUV", 90.0, "Agence Lyon", 5);
        vehicles.put(v3.getId(), v3);
        
        // Véhicule Luxe
        Vehicle v4 = new Vehicle(idCounter++, "Mercedes", "Classe C", "MN-012-OP", 
                                 "LUXE", 150.0, "Agence Paris", 5);
        vehicles.put(v4.getId(), v4);
        
        // Véhicule en maintenance
        Vehicle v5 = new Vehicle(idCounter++, "Volkswagen", "Golf", "QR-345-ST", 
                                 "ECONOMIQUE", 50.0, "Agence Lyon", 5);
        v5.setStatut("EN_MAINTENANCE");
        vehicles.put(v5.getId(), v5);
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
    }
    
    public static void updateVehicle(Vehicle vehicle) {
        vehicles.put(vehicle.getId(), vehicle);
    }
    
    public static void deleteVehicle(Long id) {
        vehicles.remove(id);
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
    
    public static long getNextId() {
        return idCounter;
    }
}