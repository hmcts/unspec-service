package uk.gov.hmcts.reform.unspec.service.tasks.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.unspec.callback.CaseEvent;
import uk.gov.hmcts.reform.unspec.model.BusinessProcess;
import uk.gov.hmcts.reform.unspec.service.CoreCaseDataService;

import java.util.Map;

import static uk.gov.hmcts.reform.unspec.helpers.ExponentialRetryTimeoutHelper.calculateExponentialRetryTimeout;

@Slf4j
@RequiredArgsConstructor
@Component
public class CaseEventTaskHandler implements ExternalTaskHandler {

    private final CoreCaseDataService coreCaseDataService;

    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        final String taskName = externalTask.getTopicName();
        log.info("Job {} started", taskName);

        try {
            Map<String, Object> allVariables = externalTask.getAllVariables();
            String ccdId = (String) allVariables.get("CCD_ID");
            String eventId = (String) allVariables.get("CASE_EVENT");

            coreCaseDataService.triggerEvent(
                Long.valueOf(ccdId),
                CaseEvent.valueOf(eventId),
                Map.of(
                    "businessProcess",
                    BusinessProcess.builder().taskId(externalTask.getActivityId()).build()
                )
            );
            externalTaskService.complete(externalTask);
            log.info("Job '{}' finished", taskName);

        } catch (Exception e) {
            int maxRetries = 3;
            int remainingRetries = externalTask.getRetries() == null ? maxRetries : externalTask.getRetries();

            externalTaskService.handleFailure(
                externalTask,
                externalTask.getWorkerId(),
                "Event failed processing",
                remainingRetries - 1,
                calculateExponentialRetryTimeout(500, maxRetries, remainingRetries)
            );

            log.error("Job '{}' errored due to {}", taskName, e.getMessage());
        }
    }
}
