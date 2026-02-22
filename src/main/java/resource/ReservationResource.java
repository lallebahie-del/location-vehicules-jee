package resource;

import java.time.LocalDate;
import java.util.List;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import model.Reservation;
import service.ReservationService;

@Path("/reservations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReservationResource {

        // 🔹 Créer une réservation via GET
        @GET
        @Path("/add")
        public Response createReservation(
                        @QueryParam("clientId") Long clientId,
                        @QueryParam("vehicleId") Long vehicleId,
                        @QueryParam("dateDebut") String dateDebut,
                        @QueryParam("dateFin") String dateFin,
                        @QueryParam("avecChauffeur") boolean avecChauffeur) {

                Reservation reservation = ReservationService.createReservation(
                                clientId,
                                vehicleId,
                                LocalDate.parse(dateDebut),
                                LocalDate.parse(dateFin),
                                avecChauffeur);

                return Response.status(Response.Status.CREATED)
                                .entity(reservation)
                                .build();
        }

        // 🔹 Lister toutes les réservations
        @GET
        public Response getAllReservations() {
                List<Reservation> reservations = ReservationService.getAllReservations();
                return Response.ok(reservations).build();
        }

        // 🔹 Confirmer une réservation (via GET pour test)
        @GET
        @Path("/confirm")
        public Response confirmReservation(@QueryParam("id") Long id) {
                boolean success = ReservationService.confirmReservation(id);
                if (success)
                        return Response.ok("Réservation confirmée").build();
                return Response.status(Response.Status.BAD_REQUEST).entity("Impossible de confirmer").build();
        }

        // 🔹 Annuler une réservation (via GET pour test)
        @GET
        @Path("/cancel")
        public Response cancelReservation(
                        @QueryParam("id") Long id,
                        @QueryParam("clientId") Long clientId) {
                boolean success = ReservationService.cancelReservation(id, clientId);
                if (success)
                        return Response.ok("Réservation annulée").build();
                return Response.status(Response.Status.BAD_REQUEST).entity("Impossible d'annuler").build();
        }

        // 🔹 Lister les réservations d'un client
        @GET
        @Path("/client/{clientId}")
        public Response getClientReservations(@PathParam("clientId") Long clientId) {
                List<Reservation> reservations = ReservationService.getClientReservations(clientId);
                return Response.ok(reservations).build();
        }
}
