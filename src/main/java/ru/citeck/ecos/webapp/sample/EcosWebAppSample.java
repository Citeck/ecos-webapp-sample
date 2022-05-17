package ru.citeck.ecos.webapp.sample;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import ru.citeck.ecos.webapp.lib.spring.EcosSpringApplication;

@SpringBootApplication
@EnableDiscoveryClient
@EnableJpaRepositories({"ru.citeck.ecos.webapp.sample.domain.*.repo"})
public class EcosWebAppSample {
    /**
     * Main method, used to run the application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //DefaultProfileUtil.addDefaultProfile(app);
        new EcosSpringApplication(EcosWebAppSample.class).run(args);
    }
}
