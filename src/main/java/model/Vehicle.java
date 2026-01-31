package model;

public class Vehicle {
    private Long id;
    private String marque;
    private String modele;
    private String immatriculation;
    private String categorie; // ECONOMIQUE, CONFORT, SUV, LUXE
    private double tarifJournalier;
    private String statut; // DISPONIBLE, EN_LOCATION, EN_MAINTENANCE
    private String agence; // Agence de rattachement
    private int nombrePlaces;

    // Constructors
    public Vehicle() {
        this.statut = "DISPONIBLE";
    }

    public Vehicle(Long id, String marque, String modele, String immatriculation, 
                   String categorie, double tarifJournalier, String agence, int nombrePlaces) {
        this.id = id;
        this.marque = marque;
        this.modele = modele;
        this.immatriculation = immatriculation;
        this.categorie = categorie;
        this.tarifJournalier = tarifJournalier;
        this.statut = "DISPONIBLE";
        this.agence = agence;
        this.nombrePlaces = nombrePlaces;
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMarque() {
        return marque;
    }

    public void setMarque(String marque) {
        this.marque = marque;
    }

    public String getModele() {
        return modele;
    }

    public void setModele(String modele) {
        this.modele = modele;
    }

    public String getImmatriculation() {
        return immatriculation;
    }

    public void setImmatriculation(String immatriculation) {
        this.immatriculation = immatriculation;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public double getTarifJournalier() {
        return tarifJournalier;
    }

    public void setTarifJournalier(double tarifJournalier) {
        this.tarifJournalier = tarifJournalier;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getAgence() {
        return agence;
    }

    public void setAgence(String agence) {
        this.agence = agence;
    }

    public int getNombrePlaces() {
        return nombrePlaces;
    }

    public void setNombrePlaces(int nombrePlaces) {
        this.nombrePlaces = nombrePlaces;
    }
}