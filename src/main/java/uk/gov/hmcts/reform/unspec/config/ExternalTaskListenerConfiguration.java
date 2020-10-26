package uk.gov.hmcts.reform.unspec.config;

import org.camunda.bpm.client.ExternalTaskClient;
import org.camunda.bpm.client.interceptor.ClientRequestContext;
import org.camunda.bpm.client.interceptor.ClientRequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.reform.authorisation.filters.ServiceAuthFilter;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

@Configuration
public abstract class ExternalTaskListenerConfiguration implements AuthTokenGenerator {

    private final String baseUrl;

    public ExternalTaskListenerConfiguration(@Value("${feign.client.config.remoteRuntimeService.url}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Bean
    public ExternalTaskClient client() {
        return ExternalTaskClient.create()
            .addInterceptor(new ServiceAuthProvider())
            .baseUrl(baseUrl)
            .build();
    }

    public class ServiceAuthProvider implements ClientRequestInterceptor {

        @Override
        public void intercept(ClientRequestContext requestContext) {
            requestContext.addHeader(ServiceAuthFilter.AUTHORISATION, generate());
        }
    }
}
