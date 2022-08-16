package ru.citeck.ecos.webapp.sample.stateless.domain.simple.api.records;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import ru.citeck.ecos.records2.predicate.PredicateService;
import ru.citeck.ecos.records2.predicate.model.Predicate;
import ru.citeck.ecos.records3.record.dao.AbstractRecordsDao;
import ru.citeck.ecos.records3.record.dao.atts.RecordAttsDao;
import ru.citeck.ecos.records3.record.dao.delete.DelStatus;
import ru.citeck.ecos.records3.record.dao.delete.RecordDeleteDao;
import ru.citeck.ecos.records3.record.dao.mutate.RecordMutateDtoDao;
import ru.citeck.ecos.records3.record.dao.query.RecordsQueryDao;
import ru.citeck.ecos.records3.record.dao.query.dto.query.QueryPage;
import ru.citeck.ecos.records3.record.dao.query.dto.query.RecordsQuery;
import ru.citeck.ecos.records3.record.dao.query.dto.res.RecsQueryRes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class SimpleRecordsDao extends AbstractRecordsDao
    implements RecordAttsDao, RecordsQueryDao,
    RecordMutateDtoDao<SimpleRecordsDao.SimpleDto>,
    RecordDeleteDao {

    public static final String ID = "simple";

    private List<SimpleDto> records = new ArrayList<>();

    @Override
    public SimpleDto getRecToMutate(@NotNull String recId) {
        if (recId.isEmpty()) {
            return new SimpleDto(UUID.randomUUID().toString(), "", 0);
        }
        return new SimpleDto(records.stream()
            .filter(it -> it.getId().equals(recId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Record with id " + recId + " is not found")));
    }

    @NotNull
    @Override
    public DelStatus delete(@NotNull String localId) {

        records = records.stream()
            .filter(it -> !it.getId().equals(localId))
            .collect(Collectors.toList());

        return DelStatus.OK;
    }

    @NotNull
    @Override
    public String saveMutatedRec(SimpleDto simpleDto) {

        SimpleDto dtoFromList = records.stream()
            .filter(it -> it.getId().equals(simpleDto.getId()))
            .findFirst()
            .orElse(null);

        if (dtoFromList != null) {
            dtoFromList.applyFields(simpleDto);
        } else {
            records.add(simpleDto);
        }
        return simpleDto.getId();
    }

    @Nullable
    @Override
    public RecsQueryRes<?> queryRecords(@NotNull RecordsQuery recordsQuery) {

        if (!PredicateService.LANGUAGE_PREDICATE.equals(recordsQuery.getLanguage())) {
            return null;
        }

        Predicate predicate = recordsQuery.getQuery(Predicate.class);

        List<SimpleDto> fullResult = predicateService.filterAndSort(
            records,
            predicate,
            recordsQuery.getSortBy(),
            0,
            -1
        );

        QueryPage page = recordsQuery.getPage();
        List<SimpleDto> result = new ArrayList<>();
        int maxItems = page.getMaxItems();
        if (maxItems == -1) {
            maxItems = Integer.MAX_VALUE;
        }
        for (int i = page.getSkipCount(); i < maxItems; i++) {
            if (i >= fullResult.size()) {
                break;
            }
            result.add(fullResult.get(i));
        }

        RecsQueryRes<SimpleDto> recsQueryRes = new RecsQueryRes<>();
        recsQueryRes.setTotalCount(fullResult.size());
        recsQueryRes.setRecords(result);

        return recsQueryRes;
    }

    @Nullable
    @Override
    public Object getRecordAtts(@NotNull String localId) {
        return records.stream()
            .filter(it -> it.id.equals(localId))
            .findFirst()
            .orElse(null);
    }

    @NotNull
    @Override
    public String getId() {
        return ID;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SimpleDto {

        private String id;
        private String field0;
        private int intField;

        public SimpleDto(SimpleDto other) {
            this.id = other.id;
            this.field0 = other.field0;
            this.intField = other.intField;
        }

        public void applyFields(SimpleDto other) {
            this.field0 = other.field0;
            this.intField = other.intField;
        }
    }
}
