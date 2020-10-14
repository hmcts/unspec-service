package uk.gov.hmcts.reform.unspec.bpmn;

import org.camunda.bpm.engine.externaltask.ExternalTask;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

class CaseTransferredToLocalCourtTest extends BpmnBaseTest {

    public static final String NOTIFY_APPLICANT_SOLICITOR_1
        = "NOTIFY_APPLICANT_SOLICITOR1_FOR_CASE_TRANSFERRED_TO_LOCAL_COURT";
    public static final String NOTIFY_RESPONDENT_SOLICITOR_1
        = "NOTIFY_RESPONDENT_SOLICITOR1_FOR_CASE_TRANSFERRED_TO_LOCAL_COURT";

    public CaseTransferredToLocalCourtTest() {
        super("case_transferred_to_local_court.bpmn", "CaseTransferredToLocalCourtHandling");
    }

    @Test
    void shouldSuccessfullyCompleteTransferToLocalCourt() {
        //assert process has started
        assertFalse(processInstance.isEnded());

        //complete the start business process
        ExternalTask startBusinessTask = getNextExternalTask(START_BUSINESS_TOPIC);
        completeExternalTask(startBusinessTask, START_BUSINESS_TOPIC, START_BUSINESS_EVENT);

        //complete the notification
        assertThat(getTopics()).containsOnly(PROCESS_CASE_EVENT_TOPIC);

        List<ExternalTask> externalTasks = getExternalTasks();
        assertThat(externalTasks).hasSize(2);

        ExternalTask notificationTask = externalTasks.get(0);
        assertThat(notificationTask.getTopicName()).isEqualTo(PROCESS_CASE_EVENT_TOPIC);
        completeExternalTask(notificationTask, PROCESS_CASE_EVENT_TOPIC, NOTIFY_APPLICANT_SOLICITOR_1);

        //complete the notification
        ExternalTask defendantNotificationTask = externalTasks.get(1);
        completeExternalTask(defendantNotificationTask, PROCESS_CASE_EVENT_TOPIC, NOTIFY_RESPONDENT_SOLICITOR_1);

        assertNoExternalTasksLeft();
    }
}
