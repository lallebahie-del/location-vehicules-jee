package resource;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import model.User;
import service.AuthService;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    // 🔹 Connexion (via GET pour test rapide)
    @GET
    @Path("/login")
    public Response login(
            @QueryParam("email") String email,
            @QueryParam("password") String password) {
        User user = AuthService.login(email, password);
        if (user != null) {
            return Response.ok(user).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Email ou mot de passe incorrect").build();
        }
    }

    // 🔹 Inscription (via GET pour test rapide)
    @GET
    @Path("/register")
    public Response register(
            @QueryParam("name") String name,
            @QueryParam("email") String email,
            @QueryParam("password") String password,
            @QueryParam("role") String role) {
        try {
            User user = AuthService.register(name, email, password, role);
            return Response.status(Response.Status.CREATED).entity(user).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}
