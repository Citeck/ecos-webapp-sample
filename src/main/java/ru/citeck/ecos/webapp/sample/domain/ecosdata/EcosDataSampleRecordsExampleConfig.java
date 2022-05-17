package ru.citeck.ecos.webapp.sample.domain.ecosdata;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.citeck.ecos.context.lib.auth.AuthContext;
import ru.citeck.ecos.data.sql.domain.DbDomainConfig;
import ru.citeck.ecos.data.sql.domain.DbDomainFactory;
import ru.citeck.ecos.data.sql.dto.DbTableRef;
import ru.citeck.ecos.data.sql.records.DbRecordsDaoConfig;
import ru.citeck.ecos.data.sql.records.perms.DbPermsComponent;
import ru.citeck.ecos.data.sql.records.perms.DbRecordPerms;
import ru.citeck.ecos.data.sql.service.DbDataServiceConfig;
import ru.citeck.ecos.model.lib.type.service.utils.TypeUtils;
import ru.citeck.ecos.records2.RecordRef;
import ru.citeck.ecos.records3.record.dao.RecordsDao;

import java.util.Collections;
import java.util.Set;

@Configuration
public class EcosDataSampleRecordsExampleConfig {

    public static final String SAMPLE_RECORDS_ID = "sample-repo";

    @Bean
    public RecordsDao createSampleRecordsDao(DbDomainFactory dbDomainFactory) {

        DbRecordPerms accessPerms = new DbRecordPerms() {
            @NotNull
            @Override
            public Set<String> getAuthoritiesWithReadPermission() {
                return Collections.singleton("EVERYONE");
            }
            @Override
            public boolean isCurrentUserHasWritePerms() {
                return AuthContext.isRunAsAdmin();
            }
        };

        DbPermsComponent permsComponent = recordRef -> accessPerms;

        RecordRef typeRef = TypeUtils.getTypeRef("sample-type");

        return dbDomainFactory.create(
            DbDomainConfig.create()
                .withRecordsDao(
                    DbRecordsDaoConfig.create()
                        .withId(SAMPLE_RECORDS_ID)
                        .withTypeRef(typeRef)
                        .build()
                )
                .withDataService(DbDataServiceConfig.create()
                    .withAuthEnabled(false)
                    .withTableRef(new DbTableRef("public", "ecos_sample_records"))
                    .withTransactional(true)
                    .withStoreTableMeta(true)
                    .build()
                ).build()
        ).withPermsComponent(permsComponent).build();
    }
}
