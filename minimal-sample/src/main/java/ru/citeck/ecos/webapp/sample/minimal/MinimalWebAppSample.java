package ru.citeck.ecos.webapp.sample.minimal;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import ru.citeck.ecos.webapp.lib.spring.EcosSpringApplication;

@SpringBootApplication
@EnableDiscoveryClient
public class MinimalWebAppSample {

    public static final String NAME = "minimal-webapp";

    /**
     * Main method, used to run the application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new EcosSpringApplication(MinimalWebAppSample.class).run(args);
    }
}
