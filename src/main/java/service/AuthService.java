package service;

import model.User;
import model.Client;
import model.AgencyManager;
import storage.UserStorage;

import java.time.LocalDate;
import java.time.Period;

public class AuthService {

    // Authentification
    public static User login(String username, String password) {
        User user = UserStorage.getUserByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    // Vérification des rôles
    public static boolean isAdmin(User user) {
        return user != null && "ADMIN".equals(user.getRole());
    }

    public static boolean isManager(User user) {
        return user != null && "MANAGER".equals(user.getRole());
    }

    public static boolean isClient(User user) {
        return user != null && "CLIENT".equals(user.getRole());
    }

    // Vérification de l'âge du client pour la catégorie
    public static boolean checkAgeForCategory(Client client, String category) {
        if (client.getDateNaissance() == null)
            return false;
        int age = Period.between(client.getDateNaissance(), LocalDate.now()).getYears();

        if ("LUXE".equals(category)) {
            return age >= 25;
        }
        // Pour toutes les autres catégories : 21 ans minimum
        return age >= 21;
    }

    // Vérification ancienneté du permis
    public static boolean checkDrivingLicenseExperience(Client client, String category) {
        if (client.getDateObtentionPermis() == null)
            return false;
        int yearsExperience = Period.between(client.getDateObtentionPermis(), LocalDate.now()).getYears();

        if ("LUXE".equals(category)) {
            return yearsExperience >= 3;
        }
        if ("SUV".equals(category)) {
            return yearsExperience >= 2;
        }
        return true;
    }

    // Vérification contraintes par catégorie
    public static String checkCategoryConstraints(Client client, String category) {
        if (!checkAgeForCategory(client, category)) {
            if ("LUXE".equals(category)) {
                return "Le client doit avoir au moins 25 ans pour la catégorie LUXE.";
            }
            return "Le client doit avoir au moins 21 ans pour louer un véhicule.";
        }
        if (!checkDrivingLicenseExperience(client, category)) {
            if ("LUXE".equals(category)) {
                return "Le permis doit avoir été obtenu depuis au moins 3 ans pour la catégorie LUXE.";
            }
            if ("SUV".equals(category)) {
                return "Le permis doit avoir été obtenu depuis au moins 2 ans pour la catégorie SUV.";
            }
        }
        return null; // Pas de problème
    }

    // Validation complète pour la location
    public static String validateRentalEligibility(Long clientId, String category) {
        User user = UserStorage.getUserById(clientId);
        if (user == null || !(user instanceof Client)) {
            return "Client non trouvé.";
        }
        Client client = (Client) user;

        if (!client.isPermisValide()) {
            return "Le permis de conduire n'est pas valide.";
        }

        String categoryCheck = checkCategoryConstraints(client, category);
        if (categoryCheck != null) {
            return categoryCheck;
        }

        return null; // Éligible
    }

    // Inscription d'un nouveau client
    public static Client registerClient(String username, String password,
            String nom, String prenom,
            LocalDate dateNaissance, LocalDate dateObtentionPermis,
            String numeroPermis, String email,
            String telephone, String adresse) {
        // Vérifier si le username existe déjà
        if (UserStorage.getUserByUsername(username) != null) {
            return null;
        }
        Client client = new Client(null, username, password, nom, prenom,
                dateNaissance, dateObtentionPermis,
                numeroPermis, email, telephone, adresse);
        UserStorage.addUser(client);
        return client;
    }

    // Inscription (version simple - compatibilité)
    public static Client registerClient(String username, String password,
            String nom, String prenom,
            LocalDate dateNaissance, LocalDate dateObtentionPermis) {
        return registerClient(username, password, nom, prenom,
                dateNaissance, dateObtentionPermis,
                null, null, null, null);
    }

    // Ajouter un gestionnaire (par l'admin)
    public static AgencyManager addManager(String username, String password,
            String nom, String prenom, String agence) {
        if (UserStorage.getUserByUsername(username) != null) {
            return null;
        }
        AgencyManager manager = new AgencyManager(null, username, password, nom, prenom, agence);
        UserStorage.addUser(manager);
        return manager;
    }

    // Modifier un gestionnaire
    public static String updateManager(Long managerId, String nom, String prenom, String agence) {
        User user = UserStorage.getUserById(managerId);
        if (user == null || !(user instanceof AgencyManager)) {
            return "Gestionnaire non trouvé.";
        }
        AgencyManager manager = (AgencyManager) user;
        if (nom != null)
            manager.setNom(nom);
        if (prenom != null)
            manager.setPrenom(prenom);
        if (agence != null)
            manager.setAgence(agence);
        UserStorage.updateUser(manager);
        return null; // Succès
    }

    // Supprimer un gestionnaire
    public static String deleteManager(Long managerId) {
        User user = UserStorage.getUserById(managerId);
        if (user == null || !(user instanceof AgencyManager)) {
            return "Gestionnaire non trouvé.";
        }
        UserStorage.deleteUser(managerId);
        return null; // Succès
    }

    // Changer le mot de passe
    public static String changePassword(Long userId, String oldPassword, String newPassword) {
        User user = UserStorage.getUserById(userId);
        if (user == null) {
            return "Utilisateur non trouvé.";
        }
        if (!user.getPassword().equals(oldPassword)) {
            return "Ancien mot de passe incorrect.";
        }
        user.setPassword(newPassword);
        UserStorage.updateUser(user);
        return null; // Succès
    }
}