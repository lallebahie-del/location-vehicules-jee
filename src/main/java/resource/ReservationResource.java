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

        // ===================== GET (lecture) =====================

        /** GET /api/reservations?userId=4 → toutes les réservations (MANAGER/ADMIN) */
        @GET
        public Response getAllReservations(@QueryParam("userId") Long userId) {
                List<Reservation> reservations = ReservationService.getAllReservations();
                return Response.ok(reservations).build();
        }

        /** GET /api/reservations/{id}?userId=4 */
        @GET
        @Path("/{id}")
        public Response getReservationById(
                        @PathParam("id") Long id,
                        @QueryParam("userId") Long userId) {
                Reservation r = storage.ReservationStorage.getReservationById(id);
                if (r == null)
                        return Response.status(Response.Status.NOT_FOUND).entity("Réservation non trouvée").build();
                return Response.ok(r).build();
        }

        /**
         * GET /api/reservations/client/{clientId}?userId=2 → réservations d'un client
         */
        @GET
        @Path("/client/{clientId}")
        public Response getClientReservations(
                        @PathParam("clientId") Long clientId,
                        @QueryParam("userId") Long userId) {
                return Response.ok(ReservationService.getClientReservations(clientId)).build();
        }

        /** GET /api/reservations/agency/{agence}?userId=4 → planning d'une agence */
        @GET
        @Path("/agency/{agence}")
        public Response getReservationsByAgency(
                        @PathParam("agence") String agence,
                        @QueryParam("userId") Long userId) {
                return Response.ok(storage.ReservationStorage.getReservationsByAgency(agence)).build();
        }

        // ===================== POST (création) =====================

        /**
         * POST /api/reservations?userId=2
         * Params: clientId, vehicleId, dateDebut (YYYY-MM-DD), dateFin (YYYY-MM-DD),
         * avecChauffeur, optionGPS, optionSiegeBebe, optionAssurance,
         * agenceDepart, agenceRetour, categorieSouhaitee
         */
        @POST
        public Response createReservation(
                        @QueryParam("userId") Long userId,
                        @QueryParam("clientId") Long clientId,
                        @QueryParam("vehicleId") Long vehicleId,
                        @QueryParam("dateDebut") String dateDebut,
                        @QueryParam("dateFin") String dateFin,
                        @QueryParam("avecChauffeur") @DefaultValue("false") boolean avecChauffeur,
                        @QueryParam("optionGPS") @DefaultValue("false") boolean optionGPS,
                        @QueryParam("optionSiegeBebe") @DefaultValue("false") boolean optionSiegeBebe,
                        @QueryParam("optionAssurance") @DefaultValue("false") boolean optionAssurance,
                        @QueryParam("agenceDepart") String agenceDepart,
                        @QueryParam("agenceRetour") String agenceRetour,
                        @QueryParam("categorieSouhaitee") String categorieSouhaitee) {
                try {
                        Reservation reservation = ReservationService.createReservation(
                                        clientId, vehicleId,
                                        LocalDate.parse(dateDebut),
                                        LocalDate.parse(dateFin),
                                        avecChauffeur);
                        // Enrichir avec les options et agences
                        reservation.setOptionGPS(optionGPS);
                        reservation.setOptionSiegeBebe(optionSiegeBebe);
                        reservation.setOptionAssurance(optionAssurance);
                        reservation.setAgenceDepart(agenceDepart);
                        reservation.setAgenceRetour(agenceRetour);
                        reservation.setCategorieSouhaitee(categorieSouhaitee);
                        storage.ReservationStorage.updateReservation(reservation);

                        return Response.status(Response.Status.CREATED).entity(reservation).build();
                } catch (Exception e) {
                        return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
                }
        }

        // ===================== PUT (modification) =====================

        /**
         * PUT /api/reservations/{id}/confirm?userId=4 → confirmer une réservation
         * (MANAGER)
         */
        @PUT
        @Path("/{id}/confirm")
        public Response confirmReservation(
                        @PathParam("id") Long id,
                        @QueryParam("userId") Long userId) {
                boolean success = ReservationService.confirmReservation(id);
                if (success)
                        return Response.ok("Réservation confirmée").build();
                return Response.status(Response.Status.BAD_REQUEST).entity("Impossible de confirmer").build();
        }

        // ===================== DELETE (annulation) =====================

        /**
         * DELETE /api/reservations/{id}?userId=2&clientId=2 → annuler une réservation
         * (CLIENT)
         */
        @DELETE
        @Path("/{id}")
        public Response cancelReservation(
                        @PathParam("id") Long id,
                        @QueryParam("userId") Long userId,
                        @QueryParam("clientId") Long clientId) {
                boolean success = ReservationService.cancelReservation(id, clientId);
                if (success)
                        return Response.ok("Réservation annulée").build();
                return Response.status(Response.Status.BAD_REQUEST).entity("Impossible d'annuler").build();
        }
}
