package model;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Reservation {
    private Long id;
    private Long clientId;
    private Long vehicleId;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private double montantTotal;
    private String statut; // EN_ATTENTE, CONFIRMEE, ANNULEE, TERMINEE
    private LocalDate dateReservation;
    private boolean avecChauffeur;
    private String agenceDepart; // Agence de départ
    private String agenceRetour; // Agence de retour (si différente)
    private boolean optionGPS; // Option GPS
    private boolean optionSiegeBebe; // Option siège bébé
    private boolean optionAssurance; // Option assurance tous risques
    private String categorieSouhaitee; // Catégorie souhaitée par le client

    // Constructors
    public Reservation() {
        this.dateReservation = LocalDate.now();
        this.statut = "EN_ATTENTE";
    }

    public Reservation(Long id, Long clientId, Long vehicleId,
            LocalDate dateDebut, LocalDate dateFin,
            double montantTotal, boolean avecChauffeur) {
        this.id = id;
        this.clientId = clientId;
        this.vehicleId = vehicleId;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.montantTotal = montantTotal;
        this.statut = "EN_ATTENTE";
        this.dateReservation = LocalDate.now();
        this.avecChauffeur = avecChauffeur;
    }

    // -- Normal Getters & Setters --
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public double getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(double montantTotal) {
        this.montantTotal = montantTotal;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public boolean isAvecChauffeur() {
        return avecChauffeur;
    }

    public void setAvecChauffeur(boolean avecChauffeur) {
        this.avecChauffeur = avecChauffeur;
    }

    public String getAgenceDepart() {
        return agenceDepart;
    }

    public void setAgenceDepart(String agenceDepart) {
        this.agenceDepart = agenceDepart;
    }

    public String getAgenceRetour() {
        return agenceRetour;
    }

    public void setAgenceRetour(String agenceRetour) {
        this.agenceRetour = agenceRetour;
    }

    public boolean isOptionGPS() {
        return optionGPS;
    }

    public void setOptionGPS(boolean optionGPS) {
        this.optionGPS = optionGPS;
    }

    public boolean isOptionSiegeBebe() {
        return optionSiegeBebe;
    }

    public void setOptionSiegeBebe(boolean optionSiegeBebe) {
        this.optionSiegeBebe = optionSiegeBebe;
    }

    public boolean isOptionAssurance() {
        return optionAssurance;
    }

    public void setOptionAssurance(boolean optionAssurance) {
        this.optionAssurance = optionAssurance;
    }

    public String getCategorieSouhaitee() {
        return categorieSouhaitee;
    }

    public void setCategorieSouhaitee(String categorieSouhaitee) {
        this.categorieSouhaitee = categorieSouhaitee;
    }

    // -- LocalDate Setters --
    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public void setDateReservation(LocalDate dateReservation) {
        this.dateReservation = dateReservation;
    }

    // -- LocalDate Getters (Hidden from Jackson) --
    @JsonIgnore
    public LocalDate getDateDebut() {
        return dateDebut;
    }

    @JsonIgnore
    public LocalDate getDateFin() {
        return dateFin;
    }

    @JsonIgnore
    public LocalDate getDateReservation() {
        return dateReservation;
    }

    // -- Jackson Friendly String Getters --
    @JsonProperty("dateDebut")
    public String getDateDebutAsString() {
        return dateDebut != null ? dateDebut.toString() : null;
    }

    @JsonProperty("dateFin")
    public String getDateFinAsString() {
        return dateFin != null ? dateFin.toString() : null;
    }

    @JsonProperty("dateReservation")
    public String getDateReservationAsString() {
        return dateReservation != null ? dateReservation.toString() : null;
    }
}
