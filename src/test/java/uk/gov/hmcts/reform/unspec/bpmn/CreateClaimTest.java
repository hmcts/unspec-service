package uk.gov.hmcts.reform.unspec.bpmn;

import org.camunda.bpm.engine.externaltask.ExternalTask;
import org.camunda.bpm.engine.externaltask.LockedExternalTask;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

class CreateClaimTest extends BpmnBaseTest {

    public static final String NOTIFY_RESPONDENT_SOLICITOR_1 = "NOTIFY_RESPONDENT_SOLICITOR1_FOR_CLAIM_ISSUE";
    private static final String NOTIFY_RESPONDENT_SOLICITOR_1_ACTIVITY_ID = "CreateClaimNotifyRespondentSolicitor1";
    private static final String MAKE_PAYMENT_ACTIVITY_ID = "CreateClaimMakePayment";
    public static final String PROCESS_PAYMENT = "processPayment";

    public CreateClaimTest() {
        super("create_claim.bpmn", "CREATE_CLAIM_PROCESS_ID");
    }

    @Test
    void shouldSuccessfullyCompleteCreateClaim() {
        //assert process has started
        assertFalse(processInstance.isEnded());

        //assert message start event
        assertThat(getProcessDefinitionByMessage("CREATE_CLAIM").getKey())
            .isEqualTo("CREATE_CLAIM_PROCESS_ID");

        //complete the start business process
        ExternalTask startBusiness = assertNextExternalTask(START_BUSINESS_TOPIC);
        assertCompleteExternalTask(startBusiness, START_BUSINESS_TOPIC, START_BUSINESS_EVENT, START_BUSINESS_ACTIVITY);

        //complete the payment
        ExternalTask paymentTask = assertNextExternalTask(PROCESS_PAYMENT);
        assertThat(paymentTask.getTopicName()).isEqualTo(PROCESS_PAYMENT);

        List<LockedExternalTask> lockedProcessTask = fetchAndLockTask(PROCESS_PAYMENT);
        assertThat(lockedProcessTask).hasSize(1);
        assertThat(lockedProcessTask.get(0).getActivityId()).isEqualTo(MAKE_PAYMENT_ACTIVITY_ID);
        completeTask(lockedProcessTask.get(0).getId());

        //complete the notification
        ExternalTask notificationTask = assertNextExternalTask(PROCESS_CASE_EVENT);
        assertCompleteExternalTask(notificationTask, PROCESS_CASE_EVENT, NOTIFY_RESPONDENT_SOLICITOR_1,
                                   NOTIFY_RESPONDENT_SOLICITOR_1_ACTIVITY_ID);

        assertNoExternalTasksLeft();
    }
}
