package resource;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import model.User;
import storage.UserStorage;

import java.io.IOException;

/**
 * Filtre de sécurité JAX-RS — contrôle d'accès basé sur les rôles.
 *
 * Tous les endpoints sécurisés nécessitent le paramètre ?userId=X.
 * Rôles : ADMIN, MANAGER, CLIENT
 */
@Provider
public class SecurityFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext ctx) throws IOException {

        String path = ctx.getUriInfo().getPath();
        String method = ctx.getMethod(); // GET, POST, PUT, DELETE

        // Normaliser le chemin
        if (path.startsWith("/"))
            path = path.substring(1);
        if (path.endsWith("/"))
            path = path.substring(0, path.length() - 1);

        // ─── ENDPOINTS PUBLICS (sans authentification) ───────────────────────
        if (isPublicEndpoint(path, method))
            return;

        // ─── RÉCUPÉRER L'UTILISATEUR ─────────────────────────────────────────
        String userIdParam = ctx.getUriInfo().getQueryParameters().getFirst("userId");
        if (userIdParam == null || userIdParam.isEmpty()) {
            ctx.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Authentification requise — fournir le paramètre 'userId'.").build());
            return;
        }

        Long userId;
        try {
            userId = Long.parseLong(userIdParam);
        } catch (NumberFormatException e) {
            ctx.abortWith(Response.status(Response.Status.BAD_REQUEST)
                    .entity("'userId' doit être un nombre entier.").build());
            return;
        }

        User user = UserStorage.getUserById(userId);
        if (user == null) {
            ctx.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Utilisateur non trouvé.").build());
            return;
        }

        String role = user.getRole();

        // ─── VÉRIFIER LES DROITS D'ACCÈS ─────────────────────────────────────
        if (!isAuthorized(path, method, role)) {
            ctx.abortWith(Response.status(Response.Status.FORBIDDEN)
                    .entity("Accès refusé — rôle '" + role + "' non autorisé.").build());
        }
    }

    // =========================================================================
    // Endpoints publics (aucun token requis)
    // =========================================================================
    private boolean isPublicEndpoint(String path, String method) {
        // Auth : login (GET) est public, register (POST) est public
        if (path.equals("auth/login"))
            return true;
        if (path.equals("auth/register"))
            return true;

        // Catalogue de véhicules en lecture seule → GET uniquement
        if ("GET".equals(method)) {
            if (path.equals("vehicles"))
                return true;
            if (path.matches("vehicles/\\d+"))
                return true;
            if (path.equals("vehicles/available"))
                return true;
            if (path.startsWith("vehicles/category/"))
                return true;
            if (path.startsWith("vehicles/agency/"))
                return true;
            if (path.equals("vehicles/pricing"))
                return true;
        }

        return false;
    }

    // =========================================================================
    // Règles d'autorisation par rôle et méthode HTTP
    // =========================================================================
    private boolean isAuthorized(String path, String method, String role) {
        boolean isAdmin = "ADMIN".equals(role);
        boolean isManager = "MANAGER".equals(role);
        boolean isClient = "CLIENT".equals(role);

        // ── VÉHICULES ──────────────────────────────────────────────────────────
        // POST /vehicles (créer) → ADMIN
        if ("vehicles".equals(path) && "POST".equals(method))
            return isAdmin;

        // PUT /vehicles/{id} (modifier) → ADMIN
        if (path.matches("vehicles/\\d+") && "PUT".equals(method))
            return isAdmin;

        // DELETE /vehicles/{id} → ADMIN
        if (path.matches("vehicles/\\d+") && "DELETE".equals(method))
            return isAdmin;

        // GET /vehicles/maintenance → MANAGER | ADMIN
        if ("vehicles/maintenance".equals(path) && "GET".equals(method))
            return isManager || isAdmin;

        // PUT /vehicles/{id}/maintenance → MANAGER | ADMIN
        if (path.matches("vehicles/\\d+/maintenance") && "PUT".equals(method))
            return isManager || isAdmin;

        // PUT /vehicles/{id}/service → MANAGER | ADMIN
        if (path.matches("vehicles/\\d+/service") && "PUT".equals(method))
            return isManager || isAdmin;

        // PUT /vehicles/{id}/accidente → MANAGER | ADMIN
        if (path.matches("vehicles/\\d+/accidente") && "PUT".equals(method))
            return isManager || isAdmin;

        // PUT /vehicles/{id}/reparer → MANAGER | ADMIN
        if (path.matches("vehicles/\\d+/reparer") && "PUT".equals(method))
            return isManager || isAdmin;

        // PUT /vehicles/pricing/{cat} → ADMIN
        if (path.startsWith("vehicles/pricing/") && "PUT".equals(method))
            return isAdmin;

        // ── RÉSERVATIONS ───────────────────────────────────────────────────────
        // GET /reservations → MANAGER | ADMIN
        if ("reservations".equals(path) && "GET".equals(method))
            return isManager || isAdmin;

        // POST /reservations → CLIENT
        if ("reservations".equals(path) && "POST".equals(method))
            return isClient;

        // GET /reservations/{id} → CLIENT | MANAGER | ADMIN
        if (path.matches("reservations/\\d+") && "GET".equals(method))
            return isClient || isManager || isAdmin;

        // PUT /reservations/{id}/confirm → MANAGER | ADMIN
        if (path.matches("reservations/\\d+/confirm") && "PUT".equals(method))
            return isManager || isAdmin;

        // DELETE /reservations/{id} → CLIENT
        if (path.matches("reservations/\\d+") && "DELETE".equals(method))
            return isClient;

        // GET /reservations/client/{id} → CLIENT | MANAGER | ADMIN
        if (path.startsWith("reservations/client/") && "GET".equals(method))
            return isClient || isManager || isAdmin;

        // GET /reservations/agency/{name} → MANAGER | ADMIN
        if (path.startsWith("reservations/agency/") && "GET".equals(method))
            return isManager || isAdmin;

        // ── CONTRATS ────────────────────────────────────────────────────────────
        // GET /contracts → MANAGER | ADMIN
        if ("contracts".equals(path) && "GET".equals(method))
            return isManager || isAdmin;

        // GET /contracts/active → MANAGER | ADMIN
        if ("contracts/active".equals(path) && "GET".equals(method))
            return isManager || isAdmin;

        // POST /contracts → MANAGER | ADMIN
        if ("contracts".equals(path) && "POST".equals(method))
            return isManager || isAdmin;

        // PUT /contracts/{id}/close → MANAGER | ADMIN
        if (path.matches("contracts/\\d+/close") && "PUT".equals(method))
            return isManager || isAdmin;

        // GET /contracts/{id} → CLIENT | MANAGER | ADMIN
        if (path.matches("contracts/\\d+") && "GET".equals(method))
            return isClient || isManager || isAdmin;

        // GET /contracts/client/{id} → CLIENT | MANAGER | ADMIN
        if (path.startsWith("contracts/client/") && "GET".equals(method))
            return isClient || isManager || isAdmin;

        // GET /contracts/overdue → MANAGER | ADMIN
        if ("contracts/overdue".equals(path) && "GET".equals(method))
            return isManager || isAdmin;

        // ── FACTURES ────────────────────────────────────────────────────────────
        // GET /invoices → MANAGER | ADMIN
        if ("invoices".equals(path) && "GET".equals(method))
            return isManager || isAdmin;

        // GET /invoices/{id} → CLIENT | MANAGER | ADMIN
        if (path.matches("invoices/\\d+") && "GET".equals(method))
            return isClient || isManager || isAdmin;

        // GET /invoices/client/{id} → CLIENT | MANAGER | ADMIN
        if (path.startsWith("invoices/client/") && "GET".equals(method))
            return isClient || isManager || isAdmin;

        // GET /invoices/stats → ADMIN
        if ("invoices/stats".equals(path) && "GET".equals(method))
            return isAdmin;

        // PUT /invoices/{id}/pay → CLIENT
        if (path.matches("invoices/\\d+/pay") && "PUT".equals(method))
            return isClient;

        // ── RAPPORTS ────────────────────────────────────────────────────────────
        // Tous les rapports : ADMIN uniquement
        if (path.startsWith("reports") && "GET".equals(method))
            return isAdmin;

        // ── CHANGEMENT MOT DE PASSE ──────────────────────────────────────────
        // PUT /auth/change-password → CLIENT | MANAGER | ADMIN
        if ("auth/change-password".equals(path) && "PUT".equals(method))
            return isClient || isManager || isAdmin;

        // Par défaut : refusé
        return false;
    }
}
