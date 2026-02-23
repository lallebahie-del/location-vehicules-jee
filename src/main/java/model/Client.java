package model;

import java.time.LocalDate;

public class Client extends User {
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private LocalDate dateObtentionPermis;
    private boolean permisValide;
    private String numeroPermis; // Numéro de permis de conduire
    private String email; // Adresse e-mail
    private String telephone; // Numéro de téléphone
    private String adresse; // Adresse complète

    public Client() {
        super();
        this.setRole("CLIENT");
    }

    public Client(Long id, String username, String password,
            String nom, String prenom,
            LocalDate dateNaissance, LocalDate dateObtentionPermis) {
        super(id, username, password, "CLIENT");
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.dateObtentionPermis = dateObtentionPermis;
        this.permisValide = true;
    }

    public Client(Long id, String username, String password,
            String nom, String prenom,
            LocalDate dateNaissance, LocalDate dateObtentionPermis,
            String numeroPermis, String email, String telephone, String adresse) {
        super(id, username, password, "CLIENT");
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.dateObtentionPermis = dateObtentionPermis;
        this.permisValide = true;
        this.numeroPermis = numeroPermis;
        this.email = email;
        this.telephone = telephone;
        this.adresse = adresse;
    }

    // Getters & Setters
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public LocalDate getDateObtentionPermis() {
        return dateObtentionPermis;
    }

    public void setDateObtentionPermis(LocalDate dateObtentionPermis) {
        this.dateObtentionPermis = dateObtentionPermis;
    }

    public boolean isPermisValide() {
        return permisValide;
    }

    public void setPermisValide(boolean permisValide) {
        this.permisValide = permisValide;
    }

    public String getNumeroPermis() {
        return numeroPermis;
    }

    public void setNumeroPermis(String numeroPermis) {
        this.numeroPermis = numeroPermis;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }
}