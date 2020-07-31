package uk.gov.hmcts.reform.unspec.config;

import org.flywaydb.core.Flyway;
import org.skife.jdbi.v2.DBI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.transaction.PlatformTransactionManager;
import uk.gov.hmcts.reform.unspec.repositories.ReferenceNumberRepository;

import javax.sql.DataSource;

@Configuration
@ConditionalOnProperty(value = "database.migration.enabled", havingValue = "false")
public class MockDatabaseConfiguration {

    @Bean
    public ReferenceNumberRepository referenceNumberRepository() {
        return () -> "000LR001";
    }
}
