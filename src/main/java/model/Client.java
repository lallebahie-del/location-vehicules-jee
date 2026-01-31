package model;

import java.time.LocalDate;

public class Client extends User {
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private LocalDate dateObtentionPermis;
    private boolean permisValide;

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
        this.permisValide = true; // par défaut
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
}