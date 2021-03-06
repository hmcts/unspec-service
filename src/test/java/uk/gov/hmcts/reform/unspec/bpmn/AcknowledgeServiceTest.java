package uk.gov.hmcts.reform.unspec.bpmn;

import org.camunda.bpm.engine.externaltask.ExternalTask;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

class AcknowledgeServiceTest extends BpmnBaseTest {

    private static final String MESSAGE_NAME = "ACKNOWLEDGE_SERVICE";
    private static final String PROCESS_ID = "ACKNOWLEDGE_SERVICE_PROCESS_ID";

    private static final String NOTIFY_APPLICANT_SOLICITOR_1
        = "NOTIFY_APPLICANT_SOLICITOR1_FOR_SERVICE_ACKNOWLEDGEMENT";
    private static final String GENERATE_ACKNOWLEDGEMENT_OF_SERVICE = "GENERATE_ACKNOWLEDGEMENT_OF_SERVICE";
    private static final String NOTIFICATION_ACTIVITY_ID = "AcknowledgeServiceNotifyApplicantSolicitor1";
    private static final String GENERATE_CERTIFICATE_ACTIVITY_ID = "AcknowledgeServiceGenerateAcknowledgementOfService";

    public AcknowledgeServiceTest() {
        super("acknowledge_service.bpmn", "ACKNOWLEDGE_SERVICE_PROCESS_ID");
    }

    @Test
    void shouldSuccessfullyCompleteAcknowledgeService() {
        //assert process has started
        assertFalse(processInstance.isEnded());

        //assert message start event
        assertThat(getProcessDefinitionByMessage(MESSAGE_NAME).getKey()).isEqualTo(PROCESS_ID);

        //complete the start business process
        ExternalTask startBusiness = assertNextExternalTask(START_BUSINESS_TOPIC);
        assertCompleteExternalTask(startBusiness, START_BUSINESS_TOPIC, START_BUSINESS_EVENT, START_BUSINESS_ACTIVITY);

        //complete the document generation
        ExternalTask documentGeneration = assertNextExternalTask(PROCESS_CASE_EVENT);
        assertCompleteExternalTask(documentGeneration, PROCESS_CASE_EVENT, GENERATE_ACKNOWLEDGEMENT_OF_SERVICE,
                                   GENERATE_CERTIFICATE_ACTIVITY_ID);

        //complete the notification to applicant
        ExternalTask notification = assertNextExternalTask(PROCESS_CASE_EVENT);
        assertCompleteExternalTask(notification, PROCESS_CASE_EVENT, NOTIFY_APPLICANT_SOLICITOR_1,
                                   NOTIFICATION_ACTIVITY_ID);

        //end business process
        ExternalTask endBusinessProcess = assertNextExternalTask(END_BUSINESS_PROCESS);
        completeBusinessProcess(endBusinessProcess);

        assertNoExternalTasksLeft();
    }

    @Test
    void shouldAbort_whenStartBusinessProcessThrowsAnError() {
        //assert process has started
        assertFalse(processInstance.isEnded());

        //assert message start event
        assertThat(getProcessDefinitionByMessage(MESSAGE_NAME).getKey()).isEqualTo(PROCESS_ID);

        VariableMap variables = Variables.createVariables();

        //fail the start business process
        ExternalTask startBusiness = assertNextExternalTask(START_BUSINESS_TOPIC);
        assertFailExternalTask(startBusiness, START_BUSINESS_TOPIC, START_BUSINESS_EVENT, START_BUSINESS_ACTIVITY);

        assertNoExternalTasksLeft();
    }
}
