package resource;

import java.util.List;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import model.Vehicle;
import service.VehicleService;

@Path("/vehicles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VehicleResource {

    // 🔹 Lister tous les véhicules
    @GET
    public Response getAllVehicles() {
        List<Vehicle> vehicles = VehicleService.getAllVehicles();
        return Response.ok(vehicles).build();
    }

    // 🔹 Récupérer un véhicule par ID
    @GET
    @Path("/{id}")
    public Response getVehicleById(@PathParam("id") Long id) {
        Vehicle vehicle = VehicleService.getVehicleById(id);
        if (vehicle == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Véhicule non trouvé")
                    .build();
        }
        return Response.ok(vehicle).build();
    }
}
