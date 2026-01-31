package model;

public class AgencyManager extends User {
    private String nom;
    private String prenom;
    private String agence;

    public AgencyManager() {
        super();
        this.setRole("MANAGER");
    }

    public AgencyManager(Long id, String username, String password, 
                         String nom, String prenom, String agence) {
        super(id, username, password, "MANAGER");
        this.nom = nom;
        this.prenom = prenom;
        this.agence = agence;
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

    public String getAgence() {
        return agence;
    }

    public void setAgence(String agence) {
        this.agence = agence;
    }
}