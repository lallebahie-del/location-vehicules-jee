package resource;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/api")
public class RestApplication extends Application {
    // JAX-RS scanne automatiquement les classes annotées @Path du même package et sous-packages
}
