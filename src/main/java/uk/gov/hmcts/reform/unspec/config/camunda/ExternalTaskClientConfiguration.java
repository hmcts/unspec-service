package uk.gov.hmcts.reform.unspec.config.camunda;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.client.ExternalTaskClient;
import org.camunda.bpm.client.backoff.ExponentialBackoffStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ExternalTaskClientConfiguration {
    @Value("${feign.client.config.remoteExternalTaskService.url}")
    private String baseUrl;

    @Bean
    public ExternalTaskClient externalTaskClient() {
        return ExternalTaskClient.create()
            .baseUrl(baseUrl)
            .lockDuration(6000)
            .backoffStrategy(new ExponentialBackoffStrategy(500L, 2, 30000L))
            .maxTasks(1)
            .build();
    }
}
