package ru.citeck.ecos.webapp.sample.stateless;

import lombok.Data;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.citeck.ecos.records2.RecordRef;
import ru.citeck.ecos.records2.predicate.model.Predicates;
import ru.citeck.ecos.records3.RecordsService;
import ru.citeck.ecos.records3.record.dao.query.dto.query.RecordsQuery;
import ru.citeck.ecos.webapp.lib.spring.test.extension.EcosSpringExtension;
import ru.citeck.ecos.webapp.sample.stateless.domain.simple.api.records.SimpleRecordsDao;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(EcosSpringExtension.class)
@SpringBootTest(classes = { StatelessWebAppSample.class })
public class StatelessWebAppSampleTest {

    @Autowired
    public RecordsService recordsService;

    @Test
    public void test() {

        final String field0InitialValue = "field-0-value";

        SimpleRecordsDao.SimpleDto simpleDto = new SimpleRecordsDao.SimpleDto();
        simpleDto.setId("test-id");
        simpleDto.setField0(field0InitialValue);

        RecordRef recordRef = recordsService.create(SimpleRecordsDao.ID, simpleDto);
        String field0Value = recordsService.getAtt(recordRef, "field0").asText();

        assertThat(field0Value).isEqualTo(field0InitialValue);

        SimpleRecordsDao.SimpleDto atts = recordsService.getAtts(recordRef, SimpleRecordsDao.SimpleDto.class);
        assertThat(atts.getField0()).isEqualTo(field0InitialValue);

        recordsService.delete(recordRef);
        String field0Value2 = recordsService.getAtt(recordRef, "field0").asText();
        assertThat(field0Value2).isEmpty();
    }

    @Test
    public void queryTest() {

        for (int i = 0; i < 10; i++) {
            SimpleRecordsDao.SimpleDto dto = new SimpleRecordsDao.SimpleDto();
            dto.setId("id-" + i);
            dto.setIntField(i);
            recordsService.create(SimpleRecordsDao.ID, dto);
        }
        List<RecordRef> records = recordsService.query(RecordsQuery.create()
            .withSourceId(SimpleRecordsDao.ID)
            .withQuery(Predicates.ge("intField", 5))
            .build()
        ).getRecords();

        assertEquals(5, records.size());
    }

    @Data
    static class DtoAtts {
        private String name;
        private int amount;
    }
}
