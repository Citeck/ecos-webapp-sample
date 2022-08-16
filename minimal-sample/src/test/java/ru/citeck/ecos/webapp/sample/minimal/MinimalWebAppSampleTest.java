package ru.citeck.ecos.webapp.sample.minimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import ru.citeck.ecos.webapp.lib.spring.test.extension.EcosSpringExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(EcosSpringExtension.class)
@SpringBootTest(classes = { MinimalWebAppSample.class })
public class MinimalWebAppSampleTest {

    @Test
    public void test() {
        assertThat(1 + 1).isEqualTo(2);
    }
}
