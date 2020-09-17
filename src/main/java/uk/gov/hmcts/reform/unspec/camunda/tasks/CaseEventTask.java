package uk.gov.hmcts.reform.unspec.camunda.tasks;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.client.ExternalTaskClient;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.unspec.callback.CaseEvent;
import uk.gov.hmcts.reform.unspec.model.BusinessProcess;
import uk.gov.hmcts.reform.unspec.service.CoreCaseDataService;

import java.util.Map;

@Slf4j
@Service
public class CaseEventTask implements ExternalTaskHandler {

    private final CoreCaseDataService coreCaseDataService;
    private final ExternalTaskClient externalTaskClient;

    public CaseEventTask(CoreCaseDataService coreCaseDataService, ExternalTaskClient externalTaskClient) {
        this.coreCaseDataService = coreCaseDataService;
        this.externalTaskClient = externalTaskClient;

        this.externalTaskClient
            .subscribe("processCaseEvent")
            .handler(this).open();

    }

    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        Integer retries = externalTask.getRetries();
        if (retries == null || retries == 0) {
            retries = 3;
        }
        try {
            String ccdId = (String) externalTask.getAllVariables().get("CCD_ID");
            String eventId = (String) externalTask.getAllVariables().get("CASE_EVENT");

            coreCaseDataService.triggerEvent(
                Long.valueOf(ccdId),
                CaseEvent.valueOf(eventId),
                Map.of(
                    "businessProcess",
                    BusinessProcess.builder().taskId(externalTask.getActivityId()).build()
                )
            );
            externalTaskService.complete(externalTask);

        } catch (Exception e) {
            externalTaskService.handleFailure(
                externalTask,
                externalTask.getWorkerId(),
                "Event failed processing",
                retries - 1,
                60L * 1000L
            );
        }
    }
}
