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

    // 🔹 Lister les véhicules disponibles
    @GET
    @Path("/available")
    public Response getAvailableVehicles() {
        return Response.ok(storage.VehicleStorage.getAvailableVehicles()).build();
    }

    // 🔹 Rechercher par catégorie
    @GET
    @Path("/category/{cat}")
    public Response getByCategory(@PathParam("cat") String cat) {
        return Response.ok(storage.VehicleStorage.getVehiclesByCategory(cat)).build();
    }

    // 🔹 Rechercher par agence
    @GET
    @Path("/agency/{name}")
    public Response getByAgency(@PathParam("name") String name) {
        return Response.ok(storage.VehicleStorage.getVehiclesByAgency(name)).build();
    }

    // 🔹 Ajouter un véhicule via GET (QueryParam)
    @GET
    @Path("/add")
    public Response addVehicle(
            @QueryParam("marque") String marque,
            @QueryParam("modele") String modele,
            @QueryParam("immatriculation") String immatriculation,
            @QueryParam("categorie") String categorie,
            @QueryParam("tarifJournalier") double tarif,
            @QueryParam("agence") String agence,
            @QueryParam("nombrePlaces") int places) {

        Vehicle vehicle = new Vehicle(null, marque, modele, immatriculation, categorie, tarif, agence, places);
        storage.VehicleStorage.addVehicle(vehicle);

        return Response.status(Response.Status.CREATED)
                .entity(vehicle)
                .build();
    }
}
