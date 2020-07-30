package uk.gov.hmcts.reform.unspec.config;

import org.skife.jdbi.v2.DBI;
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
        return new TransactionAwareDataSourceProxy(dataSource);
    }

    @Bean
    public PlatformTransactionManager transactionManager(
        TransactionAwareDataSourceProxy transactionAwareDataSourceProxy
    ) {
        return new DataSourceTransactionManager(transactionAwareDataSourceProxy);
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
