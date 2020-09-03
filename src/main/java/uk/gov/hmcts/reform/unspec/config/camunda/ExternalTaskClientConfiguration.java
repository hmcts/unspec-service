package uk.gov.hmcts.reform.unspec.config.camunda;

import org.camunda.bpm.client.ExternalTaskClient;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExternalTaskClientConfiguration {
    public ExternalTaskClient externalTaskClient(){
        return ExternalTaskClient.create().build();
    }
}
