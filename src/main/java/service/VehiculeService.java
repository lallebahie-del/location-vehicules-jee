package service;

import entity.Vehicule;
import java.util.ArrayList;
import java.util.List;

public class VehiculeService {

    private static List<Vehicule> vehicules = new ArrayList<>();

    static {
        vehicules.add(new Vehicule(1, "Toyota Prado", "SUV"));
        vehicules.add(new Vehicule(2, "BMW X6", "LUXE"));
    }

    public List<Vehicule> getAll() {
        return vehicules;
    }

    public Vehicule findById(int id) {
        return vehicules.stream()
                .filter(v -> v.getId() == id)
                .findFirst()
                .orElse(null);
    }
}
