package uk.gov.hmcts.reform.unspec.camunda.tasks;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.client.ExternalTaskClient;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.unspec.callback.CaseEvent;
import uk.gov.hmcts.reform.unspec.service.CoreCaseDataService;

import java.util.HashMap;

@Slf4j
@Service
public class BpaPaymentTask implements ExternalTaskHandler {

    private final CoreCaseDataService coreCaseDataService;
    private final ExternalTaskClient externalTaskClient;

    public BpaPaymentTask(CoreCaseDataService coreCaseDataService, ExternalTaskClient externalTaskClient) {
        this.coreCaseDataService = coreCaseDataService;
        this.externalTaskClient = externalTaskClient;

        this.externalTaskClient
            .subscribe("handleBpaPayment")
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
            VariableMap variables = Variables.createVariables();
            log.info("external task id {}", externalTask.getId());
            log.info("process instance id {}", externalTask.getProcessInstanceId());
            log.info("activity instance id {}", externalTask.getActivityInstanceId());
            log.info("activity id {}", externalTask.getActivityId());
            log.info("execution id {}", externalTask.getExecutionId());
            // work on task for that topic

            log.info("coming here case {}", ccdId);
            log.info("coming here event {}", eventId);
            coreCaseDataService.triggerEvent(Long.valueOf(ccdId), CaseEvent.valueOf(eventId));
            externalTaskService.complete(externalTask, new HashMap<>(variables));

        } catch (Exception e) {
            externalTaskService.handleFailure(
                externalTask,
                "externalWorkerId",
                "Address could not be validated: Address database not reachable",
                retries - 1,
                60L * 1000L
            );
        }
    }
}
