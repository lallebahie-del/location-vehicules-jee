package resource;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import model.RentalContract;
import service.ContractService;
import java.util.List;

@Path("/contracts")
@Produces(MediaType.APPLICATION_JSON)
public class ContractResource {

    // ===================== GET (lecture) =====================

    /** GET /api/contracts?userId=1 → tous les contrats (ADMIN/MANAGER) */
    @GET
    @Consumes(MediaType.WILDCARD)
    public Response getAllContracts(@QueryParam("userId") Long userId) {
        return Response.ok(storage.ContractStorage.getAllContracts().values()).build();
    }

    /** GET /api/contracts/active?userId=4 → contrats actifs seulement */
    @GET
    @Path("/active")
    @Consumes(MediaType.WILDCARD)
    public Response getActiveContracts(@QueryParam("userId") Long userId) {
        return Response.ok(storage.ContractStorage.getActiveContracts()).build();
    }

    /** GET /api/contracts/client/{clientId}?userId=2 → contrats d'un client */
    @GET
    @Path("/client/{clientId}")
    @Consumes(MediaType.WILDCARD)
    public Response getClientContracts(
            @PathParam("clientId") Long clientId,
            @QueryParam("userId") Long userId) {
        List<RentalContract> contracts = ContractService.getClientActiveContracts(clientId);
        return Response.ok(contracts).build();
    }

    /** GET /api/contracts/overdue?userId=4 → contrats en retard */
    @GET
    @Path("/overdue")
    @Consumes(MediaType.WILDCARD)
    public Response getOverdueContracts(@QueryParam("userId") Long userId) {
        List<RentalContract> contracts = ContractService.checkOverdueContracts();
        return Response.ok(contracts).build();
    }

    /** GET /api/contracts/{id}?userId=4 → un contrat par ID */
    @GET
    @Path("/{id}")
    @Consumes(MediaType.WILDCARD)
    public Response getContractById(
            @PathParam("id") Long id,
            @QueryParam("userId") Long userId) {
        RentalContract contract = storage.ContractStorage.getContractById(id);
        if (contract == null)
            return Response.status(Response.Status.NOT_FOUND).entity("Contrat non trouvé").build();
        return Response.ok(contract).build();
    }

    // ===================== POST (création) =====================

    /**
     * POST /api/contracts?userId=4
     * Params: reservationId, managerId
     * Crée un contrat au moment du départ du client
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON + "," + MediaType.APPLICATION_FORM_URLENCODED + "," + MediaType.WILDCARD)
    public Response createContract(
            @QueryParam("userId") Long userId,
            @QueryParam("reservationId") Long reservationId,
            @QueryParam("managerId") Long managerId,
            @QueryParam("etatDepart") String etatDepart,
            @QueryParam("kilometrageDepart") @DefaultValue("0") double kilometrageDepart,
            @QueryParam("niveauCarburantDepart") @DefaultValue("1.0") double niveauCarburantDepart,
            @QueryParam("documentsVerifies") @DefaultValue("true") boolean documentsVerifies) {
        try {
            RentalContract contract = ContractService.createContractFromReservation(reservationId, managerId);
            // Enregistrement état départ
            contract.setEtatDepart(etatDepart);
            contract.setKilometrageDepart(kilometrageDepart);
            contract.setNiveauCarburantDepart(niveauCarburantDepart);
            contract.setDocumentsVerifies(documentsVerifies);
            storage.ContractStorage.updateContract(contract);
            return Response.status(Response.Status.CREATED).entity(contract).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    // ===================== PUT (modification / clôture) =====================

    /**
     * PUT /api/contracts/{id}/close?userId=4
     * Params: etat, carburant (0.0-1.0), dommages (true/false), kilometrageRetour
     * Clôture le contrat au retour du véhicule
     */
    @PUT
    @Path("/{id}/close")
    @Consumes(MediaType.WILDCARD)
    public Response closeContract(
            @PathParam("id") Long id,
            @QueryParam("userId") Long userId,
            @QueryParam("etat") @DefaultValue("BON_ETAT") String etat,
            @QueryParam("carburant") @DefaultValue("1.0") double carburant,
            @QueryParam("dommages") @DefaultValue("false") boolean dommages,
            @QueryParam("kilometrageRetour") @DefaultValue("0") double kilometrageRetour) {
        try {
            RentalContract contract = ContractService.closeContract(id, etat, carburant, dommages, kilometrageRetour);
            return Response.ok(contract).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}
