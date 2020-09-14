package uk.gov.hmcts.reform.unspec.config;

import org.camunda.bpm.client.ExternalTaskClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExternalTaskListenerConfiguration {

    private final String baseUrl;

    public ExternalTaskListenerConfiguration(@Value("${feign.client.config.remoteRuntimeService.url}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    //TODO: define a workerId, max tasks and a sensible backOffStrategy - i.e how long should I wait between long
    // polling, currently it is the default exponential back off strategy (double the value until it reaches 60000ms).
    // Default max tasks is 10, and lock time per task is 20000L. Do we need to think about authentication?

    @Bean
    public ExternalTaskClient client() {
        return ExternalTaskClient.create()
            .baseUrl(baseUrl)
            .build();
    }
}
