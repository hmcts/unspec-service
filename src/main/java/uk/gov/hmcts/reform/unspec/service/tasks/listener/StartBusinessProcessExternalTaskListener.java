package uk.gov.hmcts.reform.unspec.service.tasks.listener;

import org.camunda.bpm.client.ExternalTaskClient;
import org.camunda.bpm.client.topic.TopicSubscriptionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.unspec.service.tasks.handler.StartBusinessProcessTaskHandler;

@Component
public abstract class StartBusinessProcessExternalTaskListener implements ExternalTaskClient {

    private static final String TOPIC = "START_BUSINESS_PROCESS";

    @Autowired
    private StartBusinessProcessExternalTaskListener(StartBusinessProcessTaskHandler startBusinessProcessTaskHandler) {
        TopicSubscriptionBuilder subscriptionBuilder = subscribe(TOPIC);
        subscriptionBuilder.handler(startBusinessProcessTaskHandler).open();
    }
}
