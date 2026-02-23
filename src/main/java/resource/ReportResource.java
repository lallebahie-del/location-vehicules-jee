package resource;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import service.ReportService;

/**
 * Endpoints de rapports destinés à l'administrateur.
 * Tous ces endpoints nécessitent le rôle ADMIN (contrôlé par SecurityFilter).
 */
@Path("/reports")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReportResource {

    /** Taux d'occupation de la flotte (véhicules en location / total). */
    @GET
    @Path("/fleet-occupancy")
    public Response getFleetOccupancy(@QueryParam("userId") Long userId) {
        return Response.ok(ReportService.getFleetOccupancyReport()).build();
    }

    /** Revenus par agence. */
    @GET
    @Path("/revenue-by-agency")
    public Response getRevenueByAgency(@QueryParam("userId") Long userId) {
        return Response.ok(ReportService.getRevenueByAgency()).build();
    }

    /** Revenus par catégorie de véhicule. */
    @GET
    @Path("/revenue-by-category")
    public Response getRevenueByCategory(@QueryParam("userId") Long userId) {
        return Response.ok(ReportService.getRevenueByCategory()).build();
    }

    /** Liste des véhicules nécessitant une maintenance. */
    @GET
    @Path("/maintenance")
    public Response getMaintenanceReport(@QueryParam("userId") Long userId) {
        return Response.ok(ReportService.getMaintenanceReport()).build();
    }

    /** Classement des clients les plus fidèles (par nombre de locations). */
    @GET
    @Path("/top-clients")
    public Response getTopClients(@QueryParam("userId") Long userId) {
        return Response.ok(ReportService.getTopClientsReport()).build();
    }

    /** Rapport financier global. */
    @GET
    @Path("/financial")
    public Response getFinancialReport(@QueryParam("userId") Long userId) {
        return Response.ok(service.InvoiceService.generateFinancialReport()).build();
    }
}
