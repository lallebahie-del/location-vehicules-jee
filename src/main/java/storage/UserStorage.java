package storage;

import java.util.HashMap;
import java.util.Map;
import model.Admin;
import model.Client;
import model.AgencyManager;
import model.User;

public class UserStorage {

    private static final Map<Long, User> users = new HashMap<>();
    private static long idCounter = 1L;

    // Initialisation avec des données de test
    static {
        // Admin par défaut
        Admin admin = new Admin(idCounter++, "admin", "admin");
        users.put(admin.getId(), admin);

        // Quelques clients de test
        Client client1 = new Client(idCounter++, "client1", "pass123",
                "Dupont", "Jean",
                java.time.LocalDate.of(1990, 5, 15),
                java.time.LocalDate.of(2015, 6, 20));
        users.put(client1.getId(), client1);

        Client client2 = new Client(idCounter++, "client2", "pass123",
                "Martin", "Marie",
                java.time.LocalDate.of(1985, 8, 22),
                java.time.LocalDate.of(2010, 3, 10));
        users.put(client2.getId(), client2);

        // Gestionnaire d'agence
        AgencyManager manager1 = new AgencyManager(idCounter++, "manager1", "pass123",
                "Bernard", "Pierre", "Agence Paris");
        users.put(manager1.getId(), manager1);
    }

    // Méthodes CRUD
    public static Map<Long, User> getAllUsers() {
        return new HashMap<>(users);
    }

    public static User getUserById(Long id) {
        return users.get(id);
    }

    public static User getUserByUsername(String username) {
        return users.values().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    public static void addUser(User user) {
        if (user.getId() == null) {
            user.setId(idCounter++);
        }
        users.put(user.getId(), user);
    }

    public static void updateUser(User user) {
        users.put(user.getId(), user);
    }

    public static void deleteUser(Long id) {
        users.remove(id);
    }

    public static boolean authenticate(String username, String password) {
        User user = getUserByUsername(username);
        return user != null && user.getPassword().equals(password);
    }

    public static long getNextId() {
        return idCounter;
    }

    public static java.util.List<User> getManagers() {
        java.util.List<User> managers = new java.util.ArrayList<>();
        for (User u : users.values()) {
            if ("MANAGER".equals(u.getRole())) {
                managers.add(u);
            }
        }
        return managers;
    }

    public static java.util.List<User> getClients() {
        java.util.List<User> clients = new java.util.ArrayList<>();
        for (User u : users.values()) {
            if ("CLIENT".equals(u.getRole())) {
                clients.add(u);
            }
        }
        return clients;
    }
}