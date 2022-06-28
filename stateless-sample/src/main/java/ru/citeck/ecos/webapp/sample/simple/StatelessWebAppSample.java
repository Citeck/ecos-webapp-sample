package ru.citeck.ecos.webapp.sample.simple;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import ru.citeck.ecos.webapp.lib.spring.EcosSpringApplication;

@SpringBootApplication
@EnableDiscoveryClient
public class StatelessWebAppSample {

    public static final String NAME = "stateless-webapp";

    /**
     * Main method, used to run the application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new EcosSpringApplication(StatelessWebAppSample.class).run(args);
    }
}
