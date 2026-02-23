package storage;

import java.util.HashMap;
import java.util.Map;

/**
 * Stockage en mémoire des tarifs journaliers par catégorie de véhicule.
 * Permet à l'administrateur de définir un tarif de référence pour chaque
 * catégorie.
 */
public class CategoryPricingStorage {

    // Map<Catégorie, TarifJournalier>
    private static final Map<String, Double> categoryPricing = new HashMap<>();

    // Tarifs par défaut à l'initialisation
    static {
        categoryPricing.put("ECONOMIQUE", 40.0);
        categoryPricing.put("COMPACT", 55.0);
        categoryPricing.put("SUV", 90.0);
        categoryPricing.put("LUXE", 150.0);
        categoryPricing.put("UTILITAIRE", 70.0);
    }

    /** Retourne tous les tarifs par catégorie. */
    public static Map<String, Double> getAllPricing() {
        return new HashMap<>(categoryPricing);
    }

    /** Retourne le tarif journalier d'une catégorie (null si inconnue). */
    public static Double getPriceForCategory(String category) {
        return categoryPricing.get(category.toUpperCase());
    }

    /** Définit ou met à jour le tarif d'une catégorie (admin). */
    public static void setCategoryPrice(String category, double price) {
        categoryPricing.put(category.toUpperCase(), price);
    }

    /** Vérifie si une catégorie existe. */
    public static boolean categoryExists(String category) {
        return categoryPricing.containsKey(category.toUpperCase());
    }
}
