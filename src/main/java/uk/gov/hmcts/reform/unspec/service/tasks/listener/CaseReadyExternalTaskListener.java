package uk.gov.hmcts.reform.unspec.service.tasks.listener;

import org.camunda.bpm.client.ExternalTaskClient;
import org.camunda.bpm.client.topic.TopicSubscriptionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.unspec.service.tasks.handler.CaseReadyHandler;
import uk.gov.hmcts.reform.unspec.service.tasks.handler.CaseStayedHandler;

@Component
public class CaseReadyExternalTaskListener {

    private static final String TOPIC = "CASE_READY_FINDER";

    @Autowired
    private CaseReadyExternalTaskListener(CaseReadyHandler caseReadyHandler, ExternalTaskClient client) {
        TopicSubscriptionBuilder subscriptionBuilder = client.subscribe(TOPIC);
        subscriptionBuilder.handler(caseReadyHandler).open();
    }
}
