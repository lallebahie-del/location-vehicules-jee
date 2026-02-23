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

    // ===================== GET (lecture) =====================

    /** GET /api/vehicles */
    @GET
    public Response getAllVehicles() {
        return Response.ok(VehicleService.getAllVehicles().values()).build();
    }

    /** GET /api/vehicles/available */
    @GET
    @Path("/available")
    public Response getAvailableVehicles() {
        return Response.ok(storage.VehicleStorage.getAvailableVehicles()).build();
    }

    /** GET /api/vehicles/{id} */
    @GET
    @Path("/{id}")
    public Response getVehicleById(@PathParam("id") Long id) {
        Vehicle vehicle = VehicleService.getVehicleById(id);
        if (vehicle == null)
            return Response.status(Response.Status.NOT_FOUND).entity("Véhicule non trouvé").build();
        return Response.ok(vehicle).build();
    }

    /** GET /api/vehicles/category/{cat} */
    @GET
    @Path("/category/{cat}")
    public Response getByCategory(@PathParam("cat") String cat) {
        return Response.ok(storage.VehicleStorage.getVehiclesByCategory(cat)).build();
    }

    /** GET /api/vehicles/agency/{name} */
    @GET
    @Path("/agency/{name}")
    public Response getByAgency(@PathParam("name") String name) {
        return Response.ok(storage.VehicleStorage.getVehiclesByAgency(name)).build();
    }

    /** GET /api/vehicles/maintenance?userId=4 */
    @GET
    @Path("/maintenance")
    public Response getVehiclesEnMaintenance(@QueryParam("userId") Long userId) {
        return Response.ok(VehicleService.getVehiclesEnMaintenance()).build();
    }

    /** GET /api/vehicles/pricing?userId=1 */
    @GET
    @Path("/pricing")
    public Response getCategoryPricing(@QueryParam("userId") Long userId) {
        Map<String, Double> pricing = VehicleService.getAllCategoryPricing();
        return Response.ok(pricing).build();
    }

    // ===================== POST (création) =====================

    /**
     * POST /api/vehicles?userId=1
     * Params: marque, modele, annee, immatriculation, categorie,
     * nombrePlaces, typeCarburant, kilometrage, tarifJournalier,
     * agence, limiteKilometrage
     */
    @POST
    public Response addVehicle(
            @QueryParam("userId") Long userId,
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

    // ===================== PUT (modification) =====================

    /**
     * PUT /api/vehicles/{id}?userId=1
     * Params: marque, modele, tarifJournalier, agence, statut
     */
    @PUT
    @Path("/{id}")
    public Response updateVehicle(
            @PathParam("id") Long id,
            @QueryParam("userId") Long userId,
            @QueryParam("marque") String marque,
            @QueryParam("modele") String modele,
            @QueryParam("tarifJournalier") @DefaultValue("0") double tarif,
            @QueryParam("agence") String agence,
            @QueryParam("statut") String statut) {
        String error = VehicleService.updateVehicle(id, marque, modele, tarif, agence, statut);
        if (error != null)
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        return Response.ok("Véhicule mis à jour").build();
    }

    /** PUT /api/vehicles/{id}/maintenance?userId=4 → met en maintenance */
    @PUT
    @Path("/{id}/maintenance")
    public Response setMaintenance(@PathParam("id") Long id, @QueryParam("userId") Long userId) {
        String error = VehicleService.setMaintenance(id);
        if (error != null)
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        return Response.ok("Véhicule mis en maintenance").build();
    }

    /**
     * PUT /api/vehicles/{id}/service?userId=4 → remet en service après maintenance
     */
    @PUT
    @Path("/{id}/service")
    public Response backToService(@PathParam("id") Long id, @QueryParam("userId") Long userId) {
        String error = VehicleService.backToService(id);
        if (error != null)
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        return Response.ok("Véhicule remis en service").build();
    }

    /** PUT /api/vehicles/{id}/accidente?userId=4 → signale accidenté */
    @PUT
    @Path("/{id}/accidente")
    public Response setAccidente(@PathParam("id") Long id, @QueryParam("userId") Long userId) {
        String error = VehicleService.setAccidente(id);
        if (error != null)
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        return Response.ok("Véhicule marqué comme accidenté").build();
    }

    /** PUT /api/vehicles/{id}/reparer?userId=4 → répare et remet disponible */
    @PUT
    @Path("/{id}/reparer")
    public Response repairVehicle(@PathParam("id") Long id, @QueryParam("userId") Long userId) {
        String error = VehicleService.repairVehicle(id);
        if (error != null)
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        return Response.ok("Véhicule réparé et remis en service").build();
    }

    /**
     * PUT /api/vehicles/pricing/{categorie}?userId=1&tarif=200
     * Met à jour le tarif journalier d'une catégorie
     */
    @PUT
    @Path("/pricing/{categorie}")
    public Response setCategoryPrice(
            @PathParam("categorie") String categorie,
            @QueryParam("userId") Long userId,
            @QueryParam("tarif") double tarif) {
        String error = VehicleService.setCategoryPrice(categorie, tarif);
        if (error != null)
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        return Response.ok("Tarif de " + categorie + " mis à jour à " + tarif + "€/jour").build();
    }

    // ===================== DELETE (suppression) =====================

    /** DELETE /api/vehicles/{id}?userId=1 */
    @DELETE
    @Path("/{id}")
    public Response deleteVehicle(@PathParam("id") Long id, @QueryParam("userId") Long userId) {
        String error = VehicleService.deleteVehicle(id);
        if (error != null)
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        return Response.ok("Véhicule supprimé").build();
    }
}
