package service;

import model.User;
import storage.UserStorage;
import java.time.LocalDate;
import java.time.Period;

public class AuthService {
    
    // Authentification simple
    public static User login(String username, String password) {
        User user = UserStorage.getUserByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
    
    // Vérifier si l'utilisateur est admin
    public static boolean isAdmin(User user) {
        return user != null && "ADMIN".equals(user.getRole());
    }
    
    // Vérifier si l'utilisateur est gestionnaire
    public static boolean isManager(User user) {
        return user != null && "MANAGER".equals(user.getRole());
    }
    
    // Vérifier si l'utilisateur est client
    public static boolean isClient(User user) {
        return user != null && "CLIENT".equals(user.getRole());
    }
    
    // Vérifier l'âge minimum selon la catégorie de véhicule
    public static boolean checkAgeForCategory(LocalDate dateNaissance, String vehicleCategory) {
        if (dateNaissance == null) return false;
        
        int age = Period.between(dateNaissance, LocalDate.now()).getYears();
        
        switch (vehicleCategory.toUpperCase()) {
            case "ECONOMIQUE":
            case "CONFORT":
                return age >= 21;
            case "SUV":
            case "LUXE":
                return age >= 25;
            default:
                return age >= 21;
        }
    }
    
    // Vérifier l'ancienneté du permis (≥ 2 ans)
    public static boolean checkDrivingLicenseExperience(LocalDate dateObtentionPermis) {
        if (dateObtentionPermis == null) return false;
        
        Period experience = Period.between(dateObtentionPermis, LocalDate.now());
        return experience.getYears() >= 2 || 
               (experience.getYears() == 1 && experience.getMonths() >= 0);
    }
    
    // Vérifier si le permis est valide
    public static boolean isLicenseValid(LocalDate dateObtentionPermis, boolean permisValide) {
        return permisValide && checkDrivingLicenseExperience(dateObtentionPermis);
    }
    
    // Validation complète pour location
    public static String validateRentalEligibility(LocalDate dateNaissance, 
                                                   LocalDate dateObtentionPermis, 
                                                   boolean permisValide, 
                                                   String vehicleCategory) {
        
        if (!checkAgeForCategory(dateNaissance, vehicleCategory)) {
            return "Âge insuffisant pour cette catégorie de véhicule";
        }
        
        if (!checkDrivingLicenseExperience(dateObtentionPermis)) {
            return "Permis insuffisamment ancien (minimum 2 ans)";
        }
        
        if (!permisValide) {
            return "Permis de conduire non valide";
        }
        
        return "OK";
    }
}