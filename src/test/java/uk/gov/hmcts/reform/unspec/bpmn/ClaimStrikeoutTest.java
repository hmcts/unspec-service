package uk.gov.hmcts.reform.unspec.bpmn;

import org.camunda.bpm.engine.externaltask.ExternalTask;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static uk.gov.hmcts.reform.unspec.handler.tasks.StartBusinessProcessTaskHandler.FLOW_STATE;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.PROCEEDS_IN_HERITAGE_SYSTEM;

class ClaimStrikeoutTest extends BpmnBaseTest {

    public static final String MESSAGE_NAME = "MOVE_CLAIM_TO_STRUCK_OUT";
    public static final String PROCESS_ID = "CLAIM_STRIKEOUT_PROCESS_ID";

    public static final String NOTIFY_RESPONDENT_SOLICITOR_1 = "NOTIFY_RESPONDENT_SOLICITOR1_CASE_STRIKE_OUT";
    public static final String RESPONDENT_SOLICITOR_1_ACTIVITY_ID = "ClaimStrikeoutNotifyRespondentSolicitor1";
    public static final String NOTIFY_APPLICANT_SOLICITOR_1 = "NOTIFY_APPLICANT_SOLICITOR1_CASE_STRIKE_OUT";
    public static final String APPLICANT_SOLICITOR_1_ACTIVITY_ID = "ClaimStrikeoutNotifyApplicantSolicitor1";

    public ClaimStrikeoutTest() {
        super("claim_strikeout.bpmn", "CLAIM_STRIKEOUT_PROCESS_ID");
    }

    @Test
    void shouldSuccessfullyCompleteStrikeoutClaim() {
        //assert process has started
        assertFalse(processInstance.isEnded());

        //assert message start event
        assertThat(getProcessDefinitionByMessage(MESSAGE_NAME).getKey()).isEqualTo(PROCESS_ID);

        //complete the start business process
        ExternalTask startBusiness = assertNextExternalTask(START_BUSINESS_TOPIC);
        assertCompleteExternalTask(startBusiness, START_BUSINESS_TOPIC, START_BUSINESS_EVENT, START_BUSINESS_ACTIVITY);

        //complete the notification to respondent solicitor 1
        ExternalTask respondentNotification = assertNextExternalTask(PROCESS_CASE_EVENT);
        assertCompleteExternalTask(
            respondentNotification,
            PROCESS_CASE_EVENT,
            NOTIFY_RESPONDENT_SOLICITOR_1,
            RESPONDENT_SOLICITOR_1_ACTIVITY_ID
        );

        //complete the notification to applicant solicitor 1
        ExternalTask applicantNotification = assertNextExternalTask(PROCESS_CASE_EVENT);
        assertCompleteExternalTask(
            applicantNotification,
            PROCESS_CASE_EVENT,
            NOTIFY_APPLICANT_SOLICITOR_1,
            APPLICANT_SOLICITOR_1_ACTIVITY_ID
        );

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
        variables.putValue(FLOW_STATE, PROCEEDS_IN_HERITAGE_SYSTEM.fullName());

        //fail the start business process
        ExternalTask startBusiness = assertNextExternalTask(START_BUSINESS_TOPIC);
        assertFailExternalTask(startBusiness, START_BUSINESS_TOPIC, START_BUSINESS_EVENT, START_BUSINESS_ACTIVITY);

        assertNoExternalTasksLeft();
    }
}
