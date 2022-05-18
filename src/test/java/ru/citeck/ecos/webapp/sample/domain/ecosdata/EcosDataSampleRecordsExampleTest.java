package ru.citeck.ecos.webapp.sample.domain.ecosdata;

import lombok.Data;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.citeck.ecos.commons.data.ObjectData;
import ru.citeck.ecos.context.lib.auth.AuthContext;
import ru.citeck.ecos.records2.RecordRef;
import ru.citeck.ecos.records2.predicate.model.Predicates;
import ru.citeck.ecos.records3.RecordsService;
import ru.citeck.ecos.records3.record.dao.query.dto.query.RecordsQuery;
import ru.citeck.ecos.records3.record.dao.query.dto.res.RecsQueryRes;
import ru.citeck.ecos.webapp.lib.spring.test.extension.EcosSpringExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@ExtendWith(EcosSpringExtension.class)
public class EcosDataSampleRecordsExampleTest {

    @Autowired
    public RecordsService recordsService;

    @Test
    public void test() {

        DtoAtts testDto = new DtoAtts();
        testDto.name = "TestName";
        testDto.amount = 1000;

        // create by dto
        RecordRef dtoRecordRef = recordsService.create(
            EcosDataSampleRecordsExampleConfig.SAMPLE_RECORDS_ID,
            testDto
        ).withAppName("sample"); // todo: withAppName is redundant and will be fixed
        assertAtts(dtoRecordRef, testDto);

        // create by ObjectData
        ObjectData newRecData = ObjectData.create();
        newRecData.set("name", testDto.name);
        newRecData.set("amount", testDto.amount);

        RecordRef objRecRef = recordsService.create(
            EcosDataSampleRecordsExampleConfig.SAMPLE_RECORDS_ID,
            newRecData
        ).withAppName("sample"); // todo: withAppName is redundant and will be fixed
        assertAtts(objRecRef, testDto);

        // mutation test

        DtoAtts mutDto = testDto.getCopy();
        mutDto.name = "ChangedName";

        // permissions denied
        assertThatThrownBy(() -> recordsService.mutateAtt(dtoRecordRef, "name", mutDto.name))
            .isInstanceOf(Exception.class)
            .hasMessageContaining("Denied");

        AuthContext.runAsSystemJ(() -> {
            recordsService.mutateAtt(dtoRecordRef, "name", mutDto.name);
        });

        assertAtts(dtoRecordRef, mutDto);

        // query test

        RecsQueryRes<RecordRef> result = recordsService.query(RecordsQuery.create()
            .withSourceId(EcosDataSampleRecordsExampleConfig.SAMPLE_RECORDS_ID)
            .withQuery(Predicates.eq("name", "ChangedName"))
            .build());

        assertThat(result.getRecords().size()).isEqualTo(1);
        assertThat(result.getRecords().get(0)).isEqualTo(dtoRecordRef);

        RecsQueryRes<RecordRef> fullResult = recordsService.query(RecordsQuery.create()
            .withSourceId(EcosDataSampleRecordsExampleConfig.SAMPLE_RECORDS_ID)
            .withQuery(Predicates.alwaysTrue())
            .build());

        assertThat(fullResult.getRecords().size()).isEqualTo(2);

        assertThat(fullResult.getRecords().contains(dtoRecordRef)).isTrue();
        assertThat(fullResult.getRecords().contains(objRecRef)).isTrue();
    }

    private void assertAtts(RecordRef recordRef, DtoAtts expexted) {

        assertThat(recordsService.getAtt(recordRef, "name").asText()).isEqualTo(expexted.name);
        assertThat(recordsService.getAtt(recordRef, "amount").asInt()).isEqualTo(expexted.amount);

        DtoAtts dtoAtts = recordsService.getAtts(recordRef, DtoAtts.class);
        assertThat(dtoAtts.name).isEqualTo(expexted.name);
        assertThat(dtoAtts.amount).isEqualTo(expexted.amount);
    }

    @Data
    static class DtoAtts {

        private String name;
        private int amount;

        DtoAtts getCopy() {
            DtoAtts copy = new DtoAtts();
            copy.name = name;
            copy.amount = amount;
            return copy;
        }
    }
}
