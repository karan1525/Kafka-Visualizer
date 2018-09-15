package karan.kafkavisualizer;

import karan.kafkavisualizer.api.RestResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

import javax.ws.rs.ApplicationPath;

@Configuration
@ApplicationPath("/api")
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig(RestResource restResource) {
        register(restResource);
    }
}
