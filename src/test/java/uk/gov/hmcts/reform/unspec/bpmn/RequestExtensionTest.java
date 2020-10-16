package uk.gov.hmcts.reform.unspec.bpmn;

import org.camunda.bpm.engine.externaltask.ExternalTask;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

class RequestExtensionTest extends BpmnBaseTest {

    public static final String NOTIFY_APPLICANT_SOLICITOR_1 = "NOTIFY_APPLICANT_SOLICITOR1_FOR_REQUEST_FOR_EXTENSION";
    private static final String ACTIVITY_ID = "RequestExtensionEmailApplicantSolicitor1";

    public RequestExtensionTest() {
        super("request_extension.bpmn", "REQUEST_EXTENSION_PROCESS_ID");
    }

    @Test
    void shouldSuccessfullyCompleteRequestExtension() {
        //assert process has started
        assertFalse(processInstance.isEnded());

        //assert message start event
        assertThat(getProcessDefinitionByMessage("REQUEST_EXTENSION").getKey())
            .isEqualTo("REQUEST_EXTENSION_PROCESS_ID");

        //complete the start business process
        ExternalTask startBusinessTask = getNextExternalTask(START_BUSINESS_TOPIC);
        completeExternalTask(startBusinessTask, START_BUSINESS_TOPIC, START_BUSINESS_EVENT, START_BUSINESS_ACTIVITY);

        //complete the notification
        ExternalTask notificationTask = getNextExternalTask(PROCESS_CASE_EVENT_TOPIC);
        completeExternalTask(notificationTask, PROCESS_CASE_EVENT_TOPIC, NOTIFY_APPLICANT_SOLICITOR_1, ACTIVITY_ID);

        assertNoExternalTasksLeft();
    }
}
