package uk.gov.hmcts.reform.unspec.service.tasks.listener;

import org.camunda.bpm.client.ExternalTaskClient;
import org.camunda.bpm.client.topic.TopicSubscriptionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.unspec.service.tasks.handler.PollingEventEmitterHandler;

@Component
@ConditionalOnExpression("${polling.event.emitter.enabled:true}")
public abstract class PollingEventEmitterExternalTaskListener implements ExternalTaskClient {

    private static final String TOPIC = "POLLING_EVENT_EMITTER";

    @Autowired
    private PollingEventEmitterExternalTaskListener(PollingEventEmitterHandler pollingEventEmitterHandler) {
        TopicSubscriptionBuilder subscriptionBuilder = subscribe(TOPIC);
        subscriptionBuilder.handler(pollingEventEmitterHandler).open();
    }
}
