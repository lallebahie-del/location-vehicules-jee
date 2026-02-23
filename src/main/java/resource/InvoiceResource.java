package resource;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import model.Invoice;
import service.InvoiceService;
import java.util.List;
import java.util.Map;

@Path("/invoices")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InvoiceResource {

    // ===================== GET (lecture) =====================

    /** GET /api/invoices?userId=1 → toutes les factures (ADMIN/MANAGER) */
    @GET
    public Response getAllInvoices(@QueryParam("userId") Long userId) {
        Map<Long, Invoice> invoices = InvoiceService.getAllInvoices();
        return Response.ok(invoices.values()).build();
    }

    /** GET /api/invoices/{id}?userId=2 → une facture par ID */
    @GET
    @Path("/{id}")
    public Response getInvoiceById(
            @PathParam("id") Long id,
            @QueryParam("userId") Long userId) {
        Invoice invoice = InvoiceService.getInvoiceById(id);
        if (invoice == null)
            return Response.status(Response.Status.NOT_FOUND).entity("Facture non trouvée").build();
        return Response.ok(invoice).build();
    }

    /** GET /api/invoices/client/{clientId}?userId=2 → factures d'un client */
    @GET
    @Path("/client/{clientId}")
    public Response getClientInvoices(
            @PathParam("clientId") Long clientId,
            @QueryParam("userId") Long userId) {
        List<Invoice> invoices = InvoiceService.getClientInvoices(clientId);
        return Response.ok(invoices).build();
    }

    /** GET /api/invoices/stats?userId=1 → statistiques financières (ADMIN) */
    @GET
    @Path("/stats")
    public Response getStats(@QueryParam("userId") Long userId) {
        return Response.ok(InvoiceService.calculateFinancialStats()).build();
    }

    // ===================== PUT (paiement) =====================

    /**
     * PUT /api/invoices/{id}/pay?userId=2&modePaiement=CARTE
     * Payer une facture. modePaiement : CARTE, ESPECES, VIREMENT
     */
    @PUT
    @Path("/{id}/pay")
    public Response payInvoice(
            @PathParam("id") Long id,
            @QueryParam("userId") Long userId,
            @QueryParam("modePaiement") @DefaultValue("CARTE") String modePaiement) {
        boolean success = InvoiceService.payInvoice(id, modePaiement);
        if (success)
            return Response.ok("Facture payée avec succès").build();
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("Échec du paiement — facture introuvable ou déjà payée").build();
    }
}
