package service;

import entity.Reservation;
import entity.Vehicule;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationService {

    private static List<Reservation> reservations = new ArrayList<>();
    private static int compteur = 1;

    private VehiculeService vehiculeService = new VehiculeService();

    public Reservation creerReservation(int vehiculeId, LocalDate debut, LocalDate fin) {

        if (debut.isBefore(LocalDate.now())) {
            throw new RuntimeException("La date doit être future");
        }

        Vehicule v = vehiculeService.findById(vehiculeId);
        if (v == null || !v.isDisponible()) {
            throw new RuntimeException("Véhicule indisponible");
        }

        Reservation r = new Reservation(compteur++, vehiculeId, debut, fin);
        reservations.add(r);
        v.setDisponible(false);

        return r;
    }

    public List<Reservation> getAll() {
        return reservations;
    }

    public void annuler(int id) {
        reservations.removeIf(r -> r.getId() == id);
    }

    public void valider(int id) {
        reservations.stream()
                .filter(r -> r.getId() == id)
                .findFirst()
                .ifPresent(r -> r.setValidee(true));
    }
}
