package resource;

import entity.Reservation;
import service.ReservationService;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.time.LocalDate;
import java.util.List;

@Path("/reservations")
@Produces(MediaType.APPLICATION_JSON)
public class ReservationResource {

    private ReservationService service = new ReservationService();

    @POST
    public Reservation creer(
            @QueryParam("vehiculeId") int vehiculeId,
            @QueryParam("debut") String debut,
            @QueryParam("fin") String fin
    ) {
        return service.creerReservation(
                vehiculeId,
                LocalDate.parse(debut),
                LocalDate.parse(fin)
        );
    }

    @GET
    public List<Reservation> getAll() {
        return service.getAll();
    }

    @PUT
    @Path("/{id}/valider")
    public void valider(@PathParam("id") int id) {
        service.valider(id);
    }

    @DELETE
    @Path("/{id}")
    public void annuler(@PathParam("id") int id) {
        service.annuler(id);
    }
}
