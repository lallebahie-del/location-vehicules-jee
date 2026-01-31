package entity;

import java.time.LocalDate;

public class Reservation {

    private int id;
    private int vehiculeId;
    private LocalDate debut;
    private LocalDate fin;
    private boolean validee = false;

    public Reservation() {}

    public Reservation(int id, int vehiculeId, LocalDate debut, LocalDate fin) {
        this.id = id;
        this.vehiculeId = vehiculeId;
        this.debut = debut;
        this.fin = fin;
    }

    public int getId() {
        return id;
    }

    public int getVehiculeId() {
        return vehiculeId;
    }

    public LocalDate getDebut() {
        return debut;
    }

    public LocalDate getFin() {
        return fin;
    }

    public boolean isValidee() {
        return validee;
    }

    public void setValidee(boolean validee) {
        this.validee = validee;
    }
}
