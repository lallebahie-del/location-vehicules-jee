package resource;

import entity.Vehicule;
import service.VehiculeService;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/vehicules")
@Produces(MediaType.APPLICATION_JSON)
public class VehiculeResource {

    private VehiculeService service = new VehiculeService();

    @GET
    public List<Vehicule> getVehicules() {
        return service.getAll();
    }
}
