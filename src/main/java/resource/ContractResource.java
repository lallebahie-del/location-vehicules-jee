package resource;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import model.RentalContract;
import service.ContractService;
import java.util.List;

@Path("/contracts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ContractResource {

    // 🔹 Créer un contrat à partir d'une réservation (via GET pour test)
    @GET
    @Path("/create")
    public Response createContract(
            @QueryParam("reservationId") Long reservationId,
            @QueryParam("managerId") Long managerId) {
        try {
            RentalContract contract = ContractService.createContractFromReservation(reservationId, managerId);
            return Response.status(Response.Status.CREATED).entity(contract).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    // 🔹 Clôturer un contrat (via GET pour test)
    @GET
    @Path("/close")
    public Response closeContract(
            @QueryParam("contractId") Long contractId,
            @QueryParam("etat") String etat,
            @QueryParam("carburant") double carburant,
            @QueryParam("dommages") boolean dommages,
            @QueryParam("kilometrageRetour") @DefaultValue("0") double kilometrageRetour) {
        try {
            RentalContract contract = ContractService.closeContract(
                    contractId, etat, carburant, dommages, kilometrageRetour);
            return Response.ok(contract).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    // 🔹 Voir les contrats actifs d'un client
    @GET
    @Path("/client/{clientId}")
    public Response getClientActiveContracts(@PathParam("clientId") Long clientId) {
        List<RentalContract> contracts = ContractService.getClientActiveContracts(clientId);
        return Response.ok(contracts).build();
    }

    // 🔹 Vérifier les retards
    @GET
    @Path("/overdue")
    public Response getOverdueContracts() {
        List<RentalContract> contracts = ContractService.checkOverdueContracts();
        return Response.ok(contracts).build();
    }
}
