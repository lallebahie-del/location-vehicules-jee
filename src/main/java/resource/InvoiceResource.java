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

    // 🔹 Liste toutes les factures
    @GET
    public Response getAllInvoices() {
        Map<Long, Invoice> invoices = InvoiceService.getAllInvoices();
        return Response.ok(invoices.values()).build();
    }

    // 🔹 Voir les factures d'un client
    @GET
    @Path("/client/{clientId}")
    public Response getClientInvoices(@PathParam("clientId") Long clientId) {
        List<Invoice> invoices = InvoiceService.getClientInvoices(clientId);
        return Response.ok(invoices).build();
    }

    // 🔹 Payer une facture (via GET pour test)
    @GET
    @Path("/pay")
    public Response payInvoice(
            @QueryParam("id") Long id,
            @QueryParam("mode") String mode) {
        boolean success = InvoiceService.payInvoice(id, mode);
        if (success) {
            return Response.ok("Facture payée avec succès").build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("Échec du paiement").build();
        }
    }

    // 🔹 Voir les statistiques financières
    @GET
    @Path("/stats")
    public Response getStats() {
        return Response.ok(InvoiceService.calculateFinancialStats()).build();
    }
}
