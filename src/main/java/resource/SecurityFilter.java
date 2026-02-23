package resource;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import model.User;
import storage.UserStorage;

import java.io.IOException;

/**
 * Filtre de sécurité JAX-RS pour le contrôle d'accès basé sur les rôles.
 * 
 * Vérifie le paramètre "userId" dans chaque requête et contrôle
 * que l'utilisateur a le rôle nécessaire pour accéder à l'endpoint.
 * 
 * Rôles : ADMIN, MANAGER, CLIENT
 */
@Provider
public class SecurityFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        String path = requestContext.getUriInfo().getPath();

        // Normaliser le chemin (supprimer les slashes en début/fin)
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        // ============================
        // ENDPOINTS PUBLICS (pas d'auth)
        // ============================
        if (isPublicEndpoint(path)) {
            return; // Accès libre
        }

        // ============================
        // RÉCUPÉRER L'UTILISATEUR
        // ============================
        String userIdParam = requestContext.getUriInfo().getQueryParameters().getFirst("userId");

        if (userIdParam == null || userIdParam.isEmpty()) {
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .entity("Authentification requise. Veuillez fournir le paramètre 'userId'.")
                            .build());
            return;
        }

        Long userId;
        try {
            userId = Long.parseLong(userIdParam);
        } catch (NumberFormatException e) {
            requestContext.abortWith(
                    Response.status(Response.Status.BAD_REQUEST)
                            .entity("Le paramètre 'userId' doit être un nombre valide.")
                            .build());
            return;
        }

        User user = UserStorage.getUserById(userId);
        if (user == null) {
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .entity("Utilisateur non trouvé.")
                            .build());
            return;
        }

        String role = user.getRole();

        // ============================
        // VÉRIFIER LES DROITS D'ACCÈS
        // ============================
        if (!isAuthorized(path, role)) {
            requestContext.abortWith(
                    Response.status(Response.Status.FORBIDDEN)
                            .entity("Accès refusé. Rôle '" + role + "' non autorisé pour cette ressource.")
                            .build());
        }
    }

    /**
     * Vérifie si l'endpoint est public (accessible sans authentification).
     */
    private boolean isPublicEndpoint(String path) {
        // Auth endpoints (login, register)
        if (path.startsWith("auth")) {
            return true;
        }

        // Consultation du catalogue de véhicules (lecture seule)
        if (path.equals("vehicles")
                || path.matches("vehicles/\\d+") // vehicles/{id}
                || path.equals("vehicles/available")
                || path.startsWith("vehicles/category/")
                || path.startsWith("vehicles/agency/")) {
            return true;
        }

        return false;
    }

    /**
     * Vérifie si le rôle de l'utilisateur est autorisé pour l'endpoint donné.
     */
    private boolean isAuthorized(String path, String role) {

        // ---- VÉHICULES ----
        // Ajout / modification / suppression : ADMIN uniquement
        if (path.equals("vehicles/add") || path.equals("vehicles/update") || path.equals("vehicles/delete")) {
            return "ADMIN".equals(role);
        }
        // Maintenance : MANAGER ou ADMIN
        if (path.equals("vehicles/maintenance") || path.equals("vehicles/maintenance/set")
                || path.equals("vehicles/maintenance/clear")) {
            return "MANAGER".equals(role) || "ADMIN".equals(role);
        }
        // Véhicule accidenté / réparation : MANAGER ou ADMIN
        if (path.equals("vehicles/accidente") || path.equals("vehicles/reparer")) {
            return "MANAGER".equals(role) || "ADMIN".equals(role);
        }
        // Tarifs par catégorie : lecture MANAGER/ADMIN, modification ADMIN uniquement
        if (path.equals("vehicles/pricing")) {
            return "ADMIN".equals(role) || "MANAGER".equals(role);
        }
        if (path.equals("vehicles/pricing/set")) {
            return "ADMIN".equals(role);
        }

        // ---- RÉSERVATIONS ----
        // Créer une réservation : CLIENT uniquement
        if (path.equals("reservations/add")) {
            return "CLIENT".equals(role);
        }
        // Confirmer une réservation : MANAGER ou ADMIN
        if (path.equals("reservations/confirm")) {
            return "MANAGER".equals(role) || "ADMIN".equals(role);
        }
        // Annuler une réservation : CLIENT uniquement
        if (path.equals("reservations/cancel")) {
            return "CLIENT".equals(role);
        }
        // Réservations d'un client : CLIENT, MANAGER, ADMIN
        if (path.startsWith("reservations/client/")) {
            return "CLIENT".equals(role) || "MANAGER".equals(role) || "ADMIN".equals(role);
        }
        // Planning par agence : MANAGER ou ADMIN
        if (path.startsWith("reservations/agency/")) {
            return "MANAGER".equals(role) || "ADMIN".equals(role);
        }
        // Lister toutes les réservations : MANAGER ou ADMIN
        if (path.equals("reservations")) {
            return "MANAGER".equals(role) || "ADMIN".equals(role);
        }

        // ---- CONTRATS ----
        // Créer un contrat : MANAGER uniquement
        if (path.equals("contracts/create")) {
            return "MANAGER".equals(role);
        }
        // Clôturer un contrat : MANAGER uniquement
        if (path.equals("contracts/close")) {
            return "MANAGER".equals(role);
        }
        // Contrats en retard : MANAGER ou ADMIN
        if (path.equals("contracts/overdue")) {
            return "MANAGER".equals(role) || "ADMIN".equals(role);
        }
        // Contrats d'un client : CLIENT, MANAGER, ADMIN
        if (path.startsWith("contracts/client/")) {
            return "CLIENT".equals(role) || "MANAGER".equals(role) || "ADMIN".equals(role);
        }

        // ---- FACTURES ----
        // Toutes les factures : ADMIN ou MANAGER
        if (path.equals("invoices")) {
            return "ADMIN".equals(role) || "MANAGER".equals(role);
        }
        // Factures d'un client : CLIENT, MANAGER, ADMIN
        if (path.startsWith("invoices/client/")) {
            return "CLIENT".equals(role) || "MANAGER".equals(role) || "ADMIN".equals(role);
        }
        // Payer une facture : CLIENT uniquement
        if (path.equals("invoices/pay")) {
            return "CLIENT".equals(role);
        }
        // Statistiques financières : ADMIN uniquement
        if (path.equals("invoices/stats")) {
            return "ADMIN".equals(role);
        }

        // ---- RAPPORTS ADMIN ----
        if (path.startsWith("reports/") || path.equals("reports")) {
            return "ADMIN".equals(role);
        }

        // Par défaut : accès refusé
        return false;
    }
}
