package resource;

import java.util.List;
import java.util.Map;

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

    // 🔹 Véhicules en maintenance
    @GET
    @Path("/maintenance")
    public Response getVehiclesEnMaintenance(@QueryParam("userId") Long userId) {
        return Response.ok(VehicleService.getVehiclesEnMaintenance()).build();
    }

    // 🔹 Ajouter un véhicule (version complète)
    @GET
    @Path("/add")
    public Response addVehicle(
            @QueryParam("marque") String marque,
            @QueryParam("modele") String modele,
            @QueryParam("annee") @DefaultValue("2024") int annee,
            @QueryParam("immatriculation") String immatriculation,
            @QueryParam("categorie") String categorie,
            @QueryParam("nombrePlaces") @DefaultValue("5") int places,
            @QueryParam("typeCarburant") @DefaultValue("ESSENCE") String typeCarburant,
            @QueryParam("kilometrage") @DefaultValue("0") double kilometrage,
            @QueryParam("tarifJournalier") double tarif,
            @QueryParam("agence") String agence,
            @QueryParam("limiteKilometrage") @DefaultValue("0") double limiteKm) {

        Vehicle vehicle = VehicleService.addVehicle(marque, modele, annee,
                immatriculation, categorie, places,
                typeCarburant, kilometrage, tarif, agence);
        vehicle.setLimiteKilometrage(limiteKm);
        storage.VehicleStorage.updateVehicle(vehicle);

        return Response.status(Response.Status.CREATED).entity(vehicle).build();
    }

    // 🔹 Modifier un véhicule
    @GET
    @Path("/update")
    public Response updateVehicle(
            @QueryParam("id") Long id,
            @QueryParam("marque") String marque,
            @QueryParam("modele") String modele,
            @QueryParam("tarifJournalier") @DefaultValue("0") double tarif,
            @QueryParam("agence") String agence,
            @QueryParam("statut") String statut,
            @QueryParam("userId") Long userId) {
        String error = VehicleService.updateVehicle(id, marque, modele, tarif, agence, statut);
        if (error != null)
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        return Response.ok("Véhicule mis à jour").build();
    }

    // 🔹 Supprimer un véhicule
    @GET
    @Path("/delete")
    public Response deleteVehicle(
            @QueryParam("id") Long id,
            @QueryParam("userId") Long userId) {
        String error = VehicleService.deleteVehicle(id);
        if (error != null)
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        return Response.ok("Véhicule supprimé").build();
    }

    // 🔹 Mettre en maintenance
    @GET
    @Path("/maintenance/set")
    public Response setMaintenance(
            @QueryParam("id") Long id,
            @QueryParam("userId") Long userId) {
        String error = VehicleService.setMaintenance(id);
        if (error != null)
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        return Response.ok("Véhicule mis en maintenance").build();
    }

    // 🔹 Remettre en service après maintenance
    @GET
    @Path("/maintenance/clear")
    public Response backToService(
            @QueryParam("id") Long id,
            @QueryParam("userId") Long userId) {
        String error = VehicleService.backToService(id);
        if (error != null)
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        return Response.ok("Véhicule remis en service").build();
    }

    // 🔹 Signaler un véhicule accidenté
    @GET
    @Path("/accidente")
    public Response setAccidente(
            @QueryParam("id") Long id,
            @QueryParam("userId") Long userId) {
        String error = VehicleService.setAccidente(id);
        if (error != null)
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        return Response.ok("Véhicule marqué comme accidenté").build();
    }

    // 🔹 Remettre un véhicule accidenté en service (après réparation)
    @GET
    @Path("/reparer")
    public Response repairVehicle(
            @QueryParam("id") Long id,
            @QueryParam("userId") Long userId) {
        String error = VehicleService.repairVehicle(id);
        if (error != null)
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        return Response.ok("Véhicule réparé et remis en service").build();
    }

    // 🔹 Consulter les tarifs par catégorie (admin)
    @GET
    @Path("/pricing")
    public Response getCategoryPricing(@QueryParam("userId") Long userId) {
        Map<String, Double> pricing = VehicleService.getAllCategoryPricing();
        return Response.ok(pricing).build();
    }

    // 🔹 Définir le tarif d'une catégorie (admin)
    @GET
    @Path("/pricing/set")
    public Response setCategoryPrice(
            @QueryParam("categorie") String categorie,
            @QueryParam("tarif") double tarif,
            @QueryParam("userId") Long userId) {
        String error = VehicleService.setCategoryPrice(categorie, tarif);
        if (error != null)
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        return Response.ok("Tarif de la catégorie " + categorie + " mis à jour à " + tarif + "€/jour").build();
    }
}
