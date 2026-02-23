package model;

public class Vehicle {
    private Long id;
    private String marque;
    private String modele;
    private int anneeFabrication; // Année de fabrication
    private String immatriculation;
    private String categorie; // ECONOMIQUE, COMPACT, SUV, LUXE, UTILITAIRE
    private int nombrePlaces;
    private String typeCarburant; // ESSENCE, DIESEL, ELECTRIQUE, HYBRIDE
    private double kilometrage; // Kilométrage actuel
    private double tarifJournalier;
    private String statut; // DISPONIBLE, EN_LOCATION, EN_MAINTENANCE
    private String agence; // Agence de rattachement
    private double limiteKilometrage; // Limite km par location (0 = illimité)

    // Constructors
    public Vehicle() {
        this.statut = "DISPONIBLE";
        this.limiteKilometrage = 0;
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
        this.limiteKilometrage = 0;
    }

    public Vehicle(Long id, String marque, String modele, int anneeFabrication,
            String immatriculation, String categorie, int nombrePlaces,
            String typeCarburant, double kilometrage, double tarifJournalier,
            String agence) {
        this.id = id;
        this.marque = marque;
        this.modele = modele;
        this.anneeFabrication = anneeFabrication;
        this.immatriculation = immatriculation;
        this.categorie = categorie;
        this.nombrePlaces = nombrePlaces;
        this.typeCarburant = typeCarburant;
        this.kilometrage = kilometrage;
        this.tarifJournalier = tarifJournalier;
        this.statut = "DISPONIBLE";
        this.agence = agence;
        this.limiteKilometrage = 0;
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

    public int getAnneeFabrication() {
        return anneeFabrication;
    }

    public void setAnneeFabrication(int anneeFabrication) {
        this.anneeFabrication = anneeFabrication;
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

    public int getNombrePlaces() {
        return nombrePlaces;
    }

    public void setNombrePlaces(int nombrePlaces) {
        this.nombrePlaces = nombrePlaces;
    }

    public String getTypeCarburant() {
        return typeCarburant;
    }

    public void setTypeCarburant(String typeCarburant) {
        this.typeCarburant = typeCarburant;
    }

    public double getKilometrage() {
        return kilometrage;
    }

    public void setKilometrage(double kilometrage) {
        this.kilometrage = kilometrage;
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

    public double getLimiteKilometrage() {
        return limiteKilometrage;
    }

    public void setLimiteKilometrage(double limiteKilometrage) {
        this.limiteKilometrage = limiteKilometrage;
    }
}