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
import java.util.stream.Collectors;

public class ReservationService {

    // =========================
    // CRÉATION DE RÉSERVATION
    // =========================
    public static Reservation createReservation(
            Long clientId,
            Long vehicleId,
            LocalDate dateDebut,
            LocalDate dateFin,
            boolean avecChauffeur) {

        if (clientId == null || vehicleId == null || dateDebut == null || dateFin == null) {
            throw new IllegalArgumentException("Paramètres manquants");
        }

        String dateValidation = validateReservationDates(dateDebut, dateFin);
        if (!"OK".equals(dateValidation)) {
            throw new IllegalArgumentException(dateValidation);
        }

        if (ReservationStorage.hasActiveReservation(clientId)) {
            throw new IllegalArgumentException("Le client a déjà une réservation active");
        }

        if (!VehicleService.isVehicleAvailableForPeriod(vehicleId, dateDebut, dateFin)) {
            throw new IllegalArgumentException("Véhicule non disponible pour cette période");
        }

        Client client = (Client) UserStorage.getUserById(clientId);
        if (client == null) {
            throw new IllegalArgumentException("Client non trouvé");
        }

        Vehicle vehicle = VehicleStorage.getVehicleById(vehicleId);
        if (vehicle == null) {
            throw new IllegalArgumentException("Véhicule non trouvé");
        }

        int clientAge = (int) ChronoUnit.YEARS.between(
                client.getDateNaissance(), LocalDate.now());

        int licenseYears = (int) ChronoUnit.YEARS.between(
                client.getDateObtentionPermis(), LocalDate.now());

        String constraints = VehicleService.checkCategoryConstraints(
                vehicle.getCategorie(), clientAge, licenseYears);

        if (!"OK".equals(constraints)) {
            throw new IllegalArgumentException(constraints);
        }

        double montant = VehicleService.calculatePriceForPeriod(
                vehicleId, dateDebut, dateFin, avecChauffeur);

        Reservation reservation = new Reservation();
        reservation.setClientId(clientId);
        reservation.setVehicleId(vehicleId);
        reservation.setDateDebut(dateDebut);
        reservation.setDateFin(dateFin);
        reservation.setMontantTotal(montant);
        reservation.setAvecChauffeur(avecChauffeur);
        reservation.setStatut("EN_ATTENTE");

        ReservationStorage.addReservation(reservation);
        VehicleService.updateVehicleStatus(vehicleId, "RESERVE");

        return reservation;
    }

    // =========================
    // VALIDATION DES DATES
    // =========================
    public static String validateReservationDates(LocalDate dateDebut, LocalDate dateFin) {
        LocalDate today = LocalDate.now();

        if (dateDebut.isBefore(today.plusDays(1))) {
            return "La date de début doit être au moins demain";
        }

        if (!dateFin.isAfter(dateDebut)) {
            return "La date de fin doit être après la date de début";
        }

        long daysBetween = ChronoUnit.DAYS.between(dateDebut, dateFin);
        if (daysBetween > 30) {
            return "La durée maximale de location est de 30 jours";
        }

        return "OK";
    }

    // =========================
    // CONFIRMER RÉSERVATION
    // =========================
    public static boolean confirmReservation(Long reservationId) {
        Reservation reservation = ReservationStorage.getReservationById(reservationId);

        if (reservation == null || !"EN_ATTENTE".equals(reservation.getStatut())) {
            return false;
        }

        reservation.setStatut("CONFIRMEE");
        ReservationStorage.updateReservation(reservation);
        return true;
    }

    // =========================
    // ANNULER RÉSERVATION (48H)
    // =========================
    public static boolean cancelReservation(Long reservationId, Long clientId) {
        Reservation reservation = ReservationStorage.getReservationById(reservationId);

        if (reservation == null || !reservation.getClientId().equals(clientId)) {
            return false;
        }

        long hoursBeforeStart = ChronoUnit.HOURS.between(
                LocalDate.now().atStartOfDay(),
                reservation.getDateDebut().atStartOfDay());

        if (hoursBeforeStart < 48) {
            reservation.setMontantTotal(reservation.getMontantTotal() * 0.2);
            reservation.setStatut("ANNULEE_AVEC_FRAIS");
        } else {
            reservation.setStatut("ANNULEE");
        }

        ReservationStorage.updateReservation(reservation);
        VehicleService.updateVehicleStatus(reservation.getVehicleId(), "DISPONIBLE");

        return true;
    }

    // =========================
    // CONSULTATION
    // =========================
    public static List<Reservation> getClientReservations(Long clientId) {
        return ReservationStorage.getReservationsByClient(clientId);
    }

    public static List<Reservation> getPendingReservations() {
        return ReservationStorage.getPendingReservations();
    }

    public static List<Reservation> getAllReservations() {
        return ReservationStorage.getAllReservations()
                .values()
                .stream()
                .collect(Collectors.toList());
    }

    // =========================
    // CONTRAT
    // =========================
    public static boolean canCreateContract(Long reservationId) {
        Reservation reservation = ReservationStorage.getReservationById(reservationId);

        return reservation != null
                && "CONFIRMEE".equals(reservation.getStatut())
                && ContractStorage.getContractByReservationId(reservationId) == null;
    }
}
