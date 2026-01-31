package storage;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import model.Reservation;
import java.time.LocalDate;

public class ReservationStorage {
    
    private static final Map<Long, Reservation> reservations = new HashMap<>();
    private static long idCounter = 1L;
    
    // Initialisation avec des réservations de test
    static {
        // Réservation confirmée
        Reservation r1 = new Reservation(idCounter++, 2L, 1L, 
                                         LocalDate.now().plusDays(2), 
                                         LocalDate.now().plusDays(5), 
                                         180.0, false);
        r1.setStatut("CONFIRMEE");
        reservations.put(r1.getId(), r1);
        
        // Réservation en attente
        Reservation r2 = new Reservation(idCounter++, 3L, 2L, 
                                         LocalDate.now().plusDays(10), 
                                         LocalDate.now().plusDays(15), 
                                         325.0, true);
        reservations.put(r2.getId(), r2);
        
        // Réservation annulée (annulation > 48h)
        Reservation r3 = new Reservation(idCounter++, 2L, 3L, 
                                         LocalDate.now().plusDays(20), 
                                         LocalDate.now().plusDays(25), 
                                         450.0, false);
        r3.setStatut("ANNULEE");
        reservations.put(r3.getId(), r3);
    }
    
    // Méthodes CRUD
    public static Map<Long, Reservation> getAllReservations() {
        return new HashMap<>(reservations);
    }
    
    public static Reservation getReservationById(Long id) {
        return reservations.get(id);
    }
    
    public static void addReservation(Reservation reservation) {
        if (reservation.getId() == null) {
            reservation.setId(idCounter++);
        }
        reservations.put(reservation.getId(), reservation);
    }
    
    public static void updateReservation(Reservation reservation) {
        reservations.put(reservation.getId(), reservation);
    }
    
    public static void deleteReservation(Long id) {
        reservations.remove(id);
    }
    
    public static List<Reservation> getReservationsByClient(Long clientId) {
        List<Reservation> result = new ArrayList<>();
        for (Reservation r : reservations.values()) {
            if (clientId.equals(r.getClientId())) {
                result.add(r);
            }
        }
        return result;
    }
    
    public static List<Reservation> getReservationsByVehicle(Long vehicleId) {
        List<Reservation> result = new ArrayList<>();
        for (Reservation r : reservations.values()) {
            if (vehicleId.equals(r.getVehicleId())) {
                result.add(r);
            }
        }
        return result;
    }
    
    public static List<Reservation> getActiveReservationsByClient(Long clientId) {
        List<Reservation> result = new ArrayList<>();
        for (Reservation r : reservations.values()) {
            if (clientId.equals(r.getClientId()) && 
                ("EN_ATTENTE".equals(r.getStatut()) || "CONFIRMEE".equals(r.getStatut()))) {
                result.add(r);
            }
        }
        return result;
    }
    
    public static boolean hasActiveReservation(Long clientId) {
        return !getActiveReservationsByClient(clientId).isEmpty();
    }
    
    public static List<Reservation> getPendingReservations() {
        List<Reservation> result = new ArrayList<>();
        for (Reservation r : reservations.values()) {
            if ("EN_ATTENTE".equals(r.getStatut())) {
                result.add(r);
            }
        }
        return result;
    }
    
    public static boolean isVehicleReservedForPeriod(Long vehicleId, LocalDate startDate, LocalDate endDate) {
        for (Reservation r : reservations.values()) {
            if (vehicleId.equals(r.getVehicleId()) && 
                ("EN_ATTENTE".equals(r.getStatut()) || "CONFIRMEE".equals(r.getStatut()))) {
                
                // Vérifier si les périodes se chevauchent
                if (!(endDate.isBefore(r.getDateDebut()) || startDate.isAfter(r.getDateFin()))) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static long getNextId() {
        return idCounter;
    }
}