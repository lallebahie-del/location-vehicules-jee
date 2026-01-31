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

    // 🔹 Créer une réservation
    @POST
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
                avecChauffeur
        );

        return Response.status(Response.Status.CREATED)
                .entity(reservation)
                .build();
    }

    // 🔹 Lister toutes les réservations
    @GET
    public Response getAllReservations() {
        List<Reservation> reservations =
                ReservationService.getAllReservations();
        return Response.ok(reservations).build();
    }
}
