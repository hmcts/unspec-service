package uk.gov.hmcts.reform.unspec.bpmn;

import org.camunda.bpm.engine.externaltask.ExternalTask;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.FULL_DEFENCE;
import static uk.gov.hmcts.reform.unspec.service.tasks.handler.StartBusinessProcessTaskHandler.FLOW_STATE;

class DefendantResponseTest extends BpmnBaseTest {

    public static final String NOTIFY_APPLICANT_SOLICITOR_1 = "NOTIFY_APPLICANT_SOLICITOR1_FOR_CASE_HANDED_OFFLINE";
    public static final String DEFENDANT_RESPONSE = "NOTIFY_APPLICANT_SOLICITOR1_FOR_DEFENDANT_RESPONSE";
    private static final String RESPONDENT_ACTIVITY_ID = "DefendantResponseCaseHandedOfflineEmailRespondentSolicitor1";
    private static final String FULL_DEFENCE_ACTIVITY_ID = "DefendantResponseFullDefenceEmailApplicantSolicitor1";
    private static final String CLAIMANT_ACTIVITY_ID = "DefendantResponseCaseHandedOfflineEmailApplicantSolicitor1";
    private static final String NOTIFY_RESPONDENT_SOLICITOR_1 = "NOTIFY_RESPONDENT_SOLICITOR1_FOR_CASE_HANDED_OFFLINE";

    public DefendantResponseTest() {
        super("defendant_response.bpmn", "DEFENDANT_RESPONSE_PROCESS_ID");
    }

    @Test
    void shouldSuccessfullyCompleteOfflineDefendantResponse() {
        //assert process has started
        assertFalse(processInstance.isEnded());

        //assert message start event
        assertThat(getProcessDefinitionByMessage("DEFENDANT_RESPONSE").getKey())
            .isEqualTo("DEFENDANT_RESPONSE_PROCESS_ID");

        //complete the start business process
        ExternalTask startBusinessTask = getNextExternalTask(START_BUSINESS_TOPIC);
        VariableMap variables = Variables.createVariables();
        variables.putValue(FLOW_STATE, "MAIN.OFFLINE");
        completeExternalTask(
            startBusinessTask,
            START_BUSINESS_TOPIC,
            START_BUSINESS_EVENT,
            START_BUSINESS_ACTIVITY,
            variables
        );

        //complete the notification to respondent
        ExternalTask forRespondent = getNextExternalTask(PROCESS_CASE_EVENT_TOPIC);
        completeExternalTask(forRespondent, PROCESS_CASE_EVENT_TOPIC,
                             NOTIFY_RESPONDENT_SOLICITOR_1, RESPONDENT_ACTIVITY_ID
        );

        //complete the notification to claimant
        ExternalTask forClaimant = getNextExternalTask(PROCESS_CASE_EVENT_TOPIC);
        completeExternalTask(forClaimant, PROCESS_CASE_EVENT_TOPIC, NOTIFY_APPLICANT_SOLICITOR_1, CLAIMANT_ACTIVITY_ID);

        assertNoExternalTasksLeft();
    }

    @Test
    void shouldSuccessfullyCompleteOnlineFullDefenceResponse() {
        //assert process has started
        assertFalse(processInstance.isEnded());

        //assert message start event
        assertThat(getProcessDefinitionByMessage("DEFENDANT_RESPONSE").getKey())
            .isEqualTo("DEFENDANT_RESPONSE_PROCESS_ID");

        //complete the start business process
        ExternalTask startBusinessTask = getNextExternalTask(START_BUSINESS_TOPIC);
        VariableMap variables = Variables.createVariables();
        variables.putValue(FLOW_STATE, FULL_DEFENCE.fullName());
        completeExternalTask(
            startBusinessTask,
            START_BUSINESS_TOPIC,
            START_BUSINESS_EVENT,
            START_BUSINESS_ACTIVITY,
            variables
        );

        //complete the notification to respondent
        ExternalTask forRespondent = getNextExternalTask(PROCESS_CASE_EVENT_TOPIC);
        completeExternalTask(forRespondent, PROCESS_CASE_EVENT_TOPIC, DEFENDANT_RESPONSE, FULL_DEFENCE_ACTIVITY_ID);

        assertNoExternalTasksLeft();
    }
}
