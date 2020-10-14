package uk.gov.hmcts.reform.unspec.bpmn;

import org.camunda.bpm.engine.externaltask.ExternalTask;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class ResponseExtensionTest extends BpmnBaseTest {

    public static final String NOTIFY_RESPONDENT_SOLICITOR_1 = "NOTIFY_RESPONDENT_SOLICITOR1_FOR_EXTENSION_RESPONSE";

    public ResponseExtensionTest() {
        super("extension_response.bpmn", "ExtensionResponseHandling");
    }

    @Test
    void shouldSuccessfullyCompleteResponseExtension() {
        //assert process has started
        assertFalse(processInstance.isEnded());

        //complete the start business process
        ExternalTask startBusinessTask = getNextExternalTask(START_BUSINESS_TOPIC);
        completeExternalTask(startBusinessTask, START_BUSINESS_TOPIC, START_BUSINESS_EVENT);

        //complete the notification
        ExternalTask notificationTask = getNextExternalTask(PROCESS_CASE_EVENT_TOPIC);
        completeExternalTask(notificationTask, PROCESS_CASE_EVENT_TOPIC, NOTIFY_RESPONDENT_SOLICITOR_1);

        assertNoExternalTasksLeft();
    }
}
