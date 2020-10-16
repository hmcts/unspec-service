package uk.gov.hmcts.reform.unspec.bpmn;

import org.camunda.bpm.engine.externaltask.ExternalTask;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

class AcknowledgeServiceTest extends BpmnBaseTest {

    public static final String NOTIFY_APPLICANT_SOLICITOR_1 = "NOTIFY_APPLICANT_SOLICITOR1_FOR_SERVICE_ACKNOWLEDGEMENT";
    public static final String ACTIVITY_ID = "AcknowledgeServiceEmailApplicantSolicitor1";

    public AcknowledgeServiceTest() {
        super("acknowledge_service.bpmn", "ACKNOWLEDGE_SERVICE_PROCESS_ID");
    }

    @Test
    void shouldSuccessfullyCompleteAcknowledgeService() {
        //assert process has started
        assertFalse(processInstance.isEnded());

        //assert message start event
        assertThat(getProcessDefinitionByMessage("ACKNOWLEDGE_SERVICE").getKey())
            .isEqualTo("ACKNOWLEDGE_SERVICE_PROCESS_ID");

        //complete the start business process
        ExternalTask startBusinessTask = getNextExternalTask(START_BUSINESS_TOPIC);
        completeExternalTask(startBusinessTask, START_BUSINESS_TOPIC, START_BUSINESS_EVENT, START_BUSINESS_ACTIVITY);

        //complete the notification to claimant
        ExternalTask notification = getNextExternalTask(PROCESS_CASE_EVENT_TOPIC);
        completeExternalTask(notification, PROCESS_CASE_EVENT_TOPIC, NOTIFY_APPLICANT_SOLICITOR_1, ACTIVITY_ID);

        assertNoExternalTasksLeft();
    }
}
