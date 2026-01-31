package entity;

public class Vehicule {

    private int id;
    private String modele;
    private String categorie; // SUV ou LUXE
    private boolean disponible = true;

    public Vehicule() {}

    public Vehicule(int id, String modele, String categorie) {
        this.id = id;
        this.modele = modele;
        this.categorie = categorie;
    }

    public int getId() {
        return id;
    }

    public String getModele() {
        return modele;
    }

    public String getCategorie() {
        return categorie;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
}
