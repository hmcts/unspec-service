package uk.gov.hmcts.reform.unspec.bpmn;

import org.camunda.bpm.engine.externaltask.ExternalTask;
import org.camunda.bpm.engine.externaltask.LockedExternalTask;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ClaimIssueTaskTest extends BpmnBaseTest {

    public static final String TOPIC_NAME = "START_BUSINESS_PROCESS";

    public ClaimIssueTaskTest() {
        super("claim_issue.bpmn", "ClaimIssueHandling");
    }

    @Test
    void caseEventTaskShouldFireCaseEventExternalTask_whenStarted() {
        //assert process has started
        assertFalse(processInstance.isEnded());

        //assert topic names
        assertThat(getTopics()).containsOnly(TOPIC_NAME);

        //get external tasks
        List<ExternalTask> startBusinessProcessExternalTasks = getExternalTasks();

        //assert task is as expected
        assertThat(startBusinessProcessExternalTasks).hasSize(1);
        assertThat(startBusinessProcessExternalTasks.get(0).getTopicName()).isEqualTo("START_BUSINESS_PROCESS");

        //fetch and complete task
        List<LockedExternalTask> lockedExternalTasks = fetchAndLockTask(TOPIC_NAME);

        assertThat(lockedExternalTasks).hasSize(1);
        assertThat(lockedExternalTasks.get(0).getVariables())
            .containsEntry("caseId", "1601986692564009")
            .containsEntry("caseEvent", "START_BUSINESS_PROCESS")
        ;

        completeTask(lockedExternalTasks.get(0).getId());

        // Claim issue external task
        List<ExternalTask> claimIssueExternalTask = getExternalTasks();

        //assert task is as expected
        assertThat(claimIssueExternalTask).hasSize(1);
        assertThat(claimIssueExternalTask.get(0).getTopicName()).isEqualTo("processCaseEvent");

        //fetch and complete task
        List<LockedExternalTask> lockedClaimIssueExternalTasks = fetchAndLockTask("processCaseEvent");

        assertThat(lockedClaimIssueExternalTasks).hasSize(1);
        assertThat(lockedClaimIssueExternalTasks.get(0).getVariables())
            .containsEntry("caseId", "1601986692564009")
            .containsEntry("caseEvent", "NOTIFY_RESPONDENT_SOLICITOR1_FOR_CLAIM_ISSUE")
        ;

        completeTask(lockedClaimIssueExternalTasks.get(0).getId());

        //assert no external tasks left
        List<ExternalTask> externalTasksAfter = getExternalTasks();
        assertThat(externalTasksAfter).isEmpty();
    }
}
