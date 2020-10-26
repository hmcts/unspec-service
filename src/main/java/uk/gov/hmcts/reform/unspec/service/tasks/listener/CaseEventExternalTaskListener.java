package uk.gov.hmcts.reform.unspec.service.tasks.listener;

import org.camunda.bpm.client.ExternalTaskClient;
import org.camunda.bpm.client.topic.TopicSubscriptionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.unspec.service.tasks.handler.CaseEventTaskHandler;

@Component
public abstract class CaseEventExternalTaskListener implements ExternalTaskClient {

    private static final String TOPIC = "processCaseEvent";

    @Autowired
    private CaseEventExternalTaskListener(CaseEventTaskHandler caseEventTaskHandler) {
        TopicSubscriptionBuilder subscriptionBuilder = subscribe(TOPIC);
        subscriptionBuilder.handler(caseEventTaskHandler).open();
    }
}
