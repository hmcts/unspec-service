package uk.gov.hmcts.reform.unspec.config;

import org.flywaydb.core.Flyway;
import org.skife.jdbi.v2.DBI;
import org.springframework.beans.factory.annotation.Value;
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
public class DBConfiguration {
    @Value("${database.migration}")
    public boolean databaseMigration;

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean
    public DataSource dataSource() {
        return dataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean
    public TransactionAwareDataSourceProxy transactionAwareDataSourceProxy(DataSource dataSource) {
        TransactionAwareDataSourceProxy dataSourceProxy = new TransactionAwareDataSourceProxy(dataSource);
        if (databaseMigration) {
            migrateFlyway(dataSourceProxy);
        }
        return dataSourceProxy;
    }

    @Bean
    public PlatformTransactionManager transactionManager(
        TransactionAwareDataSourceProxy transactionAwareDataSourceProxy
    ) {
        return new DataSourceTransactionManager(transactionAwareDataSourceProxy);
    }


    private void migrateFlyway(DataSource dataSource) {
        Flyway.configure()
            .dataSource(dataSource)
            .locations("/")
            .load()
            .migrate();
    }

    @Bean
    public DBI dbi(TransactionAwareDataSourceProxy transactionAwareDataSourceProxy) {
        return new DBI(transactionAwareDataSourceProxy);
    }

    @Bean
    public ReferenceNumberRepository referenceNumberRepository(DBI dbi) {
        return dbi.onDemand(ReferenceNumberRepository.class);
    }
}
