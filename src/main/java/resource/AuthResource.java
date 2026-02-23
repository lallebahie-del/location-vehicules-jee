package resource;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import model.User;
import model.Client;
import service.AuthService;
import java.time.LocalDate;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    // GET /api/auth/login?email=admin&password=admin
    @GET
    @Path("/login")
    public Response login(
            @QueryParam("email") String email,
            @QueryParam("password") String password) {
        User user = AuthService.login(email, password);
        if (user != null) {
            return Response.ok(user).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity("Identifiant ou mot de passe incorrect").build();
    }

    /**
     * POST /api/auth/register
     * Body (query params): username, password, nom, prenom,
     * dateNaissance (YYYY-MM-DD), dateObtentionPermis (YYYY-MM-DD),
     * numeroPermis, email, telephone, adresse
     */
    @POST
    @Path("/register")
    public Response register(
            @QueryParam("username") String username,
            @QueryParam("password") String password,
            @QueryParam("nom") String nom,
            @QueryParam("prenom") String prenom,
            @QueryParam("dateNaissance") String dateNaissance,
            @QueryParam("dateObtentionPermis") String dateObtentionPermis,
            @QueryParam("numeroPermis") String numeroPermis,
            @QueryParam("email") String email,
            @QueryParam("telephone") String telephone,
            @QueryParam("adresse") String adresse) {
        try {
            Client client = AuthService.registerClient(
                    username, password, nom, prenom,
                    LocalDate.parse(dateNaissance),
                    LocalDate.parse(dateObtentionPermis),
                    numeroPermis, email, telephone, adresse);
            if (client == null) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("Ce nom d'utilisateur existe déjà").build();
            }
            return Response.status(Response.Status.CREATED).entity(client).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Erreur: " + e.getMessage()).build();
        }
    }

    /**
     * PUT
     * /api/auth/change-password?userId=2&oldPassword=pass123&newPassword=newpass
     */
    @PUT
    @Path("/change-password")
    public Response changePassword(
            @QueryParam("userId") Long userId,
            @QueryParam("oldPassword") String oldPassword,
            @QueryParam("newPassword") String newPassword) {
        String error = AuthService.changePassword(userId, oldPassword, newPassword);
        if (error != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }
        return Response.ok("Mot de passe modifié avec succès").build();
    }
}
