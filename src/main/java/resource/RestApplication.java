package resource;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class RestApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();

        // Filtres de sécurité
        classes.add(SecurityFilter.class);

        // Resources REST
        classes.add(AuthResource.class);
        classes.add(VehicleResource.class);
        classes.add(ReservationResource.class);
        classes.add(ContractResource.class);
        classes.add(InvoiceResource.class);
        classes.add(ReportResource.class);

        return classes;
    }
}
