package service;

import model.Reservation;
import model.Client;
import model.Vehicle;
import storage.ReservationStorage;
import storage.UserStorage;
import storage.VehicleStorage;
import storage.ContractStorage;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ReservationService {
    
    // Créer une nouvelle réservation avec validation
    public static Reservation createReservation(Long clientId, Long vehicleId, 
                                                LocalDate dateDebut, LocalDate dateFin, 
                                                boolean avecChauffeur) {
        
        // 1. Validation des dates
        String dateValidation = validateReservationDates(dateDebut, dateFin);
        if (!"OK".equals(dateValidation)) {
            throw new IllegalArgumentException(dateValidation);
        }
        
        // 2. Vérifier si le client a déjà une réservation active
        if (ReservationStorage.hasActiveReservation(clientId)) {
            throw new IllegalArgumentException("Le client a déjà une réservation active");
        }
        
        // 3. Vérifier la disponibilité du véhicule
        if (!VehicleService.isVehicleAvailableForPeriod(vehicleId, dateDebut, dateFin)) {
            throw new IllegalArgumentException("Véhicule non disponible pour cette période");
        }
        
        // 4. Récupérer le client pour vérifier l'éligibilité
        Client client = (Client) UserStorage.getUserById(clientId);
        if (client == null) {
            throw new IllegalArgumentException("Client non trouvé");
        }
        
        // 5. Récupérer le véhicule pour vérifier les contraintes
        Vehicle vehicle = VehicleStorage.getVehicleById(vehicleId);
        if (vehicle == null) {
            throw new IllegalArgumentException("Véhicule non trouvé");
        }
        
        // 6. Vérifier les contraintes d'âge et de permis
        int clientAge = (int) ChronoUnit.YEARS.between(client.getDateNaissance(), LocalDate.now());
        int licenseYears = (int) ChronoUnit.YEARS.between(client.getDateObtentionPermis(), LocalDate.now());
        
        String constraints = VehicleService.checkCategoryConstraints(
            vehicle.getCategorie(), clientAge, licenseYears);
        
        if (!"OK".equals(constraints)) {
            throw new IllegalArgumentException(constraints);
        }
        
        // 7. Calculer le montant
        double montant = VehicleService.calculatePriceForPeriod(vehicleId, dateDebut, dateFin, avecChauffeur);
        
        // 8. Créer la réservation
        Reservation reservation = new Reservation();
        reservation.setClientId(clientId);
        reservation.setVehicleId(vehicleId);
        reservation.setDateDebut(dateDebut);
        reservation.setDateFin(dateFin);
        reservation.setMontantTotal(montant);
        reservation.setAvecChauffeur(avecChauffeur);
        reservation.setStatut("EN_ATTENTE");
        
        ReservationStorage.addReservation(reservation);
        
        // 9. Mettre à jour le statut du véhicule
        VehicleService.updateVehicleStatus(vehicleId, "RESERVE");
        
        return reservation;
    }
    
    // Valider les dates de réservation
    public static String validateReservationDates(LocalDate dateDebut, LocalDate dateFin) {
        LocalDate today = LocalDate.now();
        
        // Date début doit être dans le futur
        if (dateDebut.isBefore(today.plusDays(1))) {
            return "La date de début doit être au moins demain";
        }
        
        // Date fin doit être après date début
        if (!dateFin.isAfter(dateDebut)) {
            return "La date de fin doit être après la date de début";
        }
        
        // Réservation maximum 30 jours
        long daysBetween = ChronoUnit.DAYS.between(dateDebut, dateFin);
        if (daysBetween > 30) {
            return "La durée maximale de location est de 30 jours";
        }
        
        return "OK";
    }
    
    // Confirmer une réservation (par un gestionnaire)
    public static boolean confirmReservation(Long reservationId, Long managerId) {
        Reservation reservation = ReservationStorage.getReservationById(reservationId);
        if (reservation == null || !"EN_ATTENTE".equals(reservation.getStatut())) {
            return false;
        }
        
        reservation.setStatut("CONFIRMEE");
        ReservationStorage.updateReservation(reservation);
        
        // Le véhicule reste "RESERVE" jusqu'à la création du contrat
        
        return true;
    }
    
    // Annuler une réservation
    public static boolean cancelReservation(Long reservationId, Long clientId) {
        Reservation reservation = ReservationStorage.getReservationById(reservationId);
        if (reservation == null || !reservation.getClientId().equals(clientId)) {
            return false;
        }
        
        // Vérifier le délai d'annulation (≥ 48h avant le début)
        LocalDate today = LocalDate.now();
        long hoursBeforeStart = ChronoUnit.HOURS.between(
            today.atStartOfDay(), 
            reservation.getDateDebut().atStartOfDay()
        );
        
        if (hoursBeforeStart < 48) {
            // Appliquer des frais d'annulation (20% du montant)
            double fraisAnnulation = reservation.getMontantTotal() * 0.2;
            reservation.setMontantTotal(fraisAnnulation);
            reservation.setStatut("ANNULEE_AVEC_FRAIS");
        } else {
            reservation.setStatut("ANNULEE");
        }
        
        ReservationStorage.updateReservation(reservation);
        
        // Libérer le véhicule
        VehicleService.updateVehicleStatus(reservation.getVehicleId(), "DISPONIBLE");
        
        return true;
    }
    
    // Récupérer les réservations d'un client
    public static List<Reservation> getClientReservations(Long clientId) {
        return ReservationStorage.getReservationsByClient(clientId);
    }
    
    // Récupérer les réservations en attente (pour les gestionnaires)
    public static List<Reservation> getPendingReservations() {
        return ReservationStorage.getPendingReservations();
    }
    
    // Vérifier si une réservation peut être transformée en contrat
    public static boolean canCreateContract(Long reservationId) {
        Reservation reservation = ReservationStorage.getReservationById(reservationId);
        if (reservation == null || !"CONFIRMEE".equals(reservation.getStatut())) {
            return false;
        }
        
        // Vérifier qu'aucun contrat n'existe déjà pour cette réservation
        return ContractStorage.getContractByReservationId(reservationId) == null;
    }
}