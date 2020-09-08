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
public class NotificationTask implements ExternalTaskHandler {

    private final CoreCaseDataService coreCaseDataService;
    private final ExternalTaskClient externalTaskClient;

    public NotificationTask(CoreCaseDataService coreCaseDataService, ExternalTaskClient externalTaskClient) {
        this.coreCaseDataService = coreCaseDataService;
        this.externalTaskClient = externalTaskClient;

        this.externalTaskClient.subscribe("sendMail").handler(this).open();
    }


    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {

        try {
            String topic = externalTask.getTopicName();
            String ccdId = (String) externalTask.getAllVariables().get("ccd_case_Id");
            String eventId = (String) externalTask.getAllVariables().get("caseEvent");
            VariableMap variables = Variables.createVariables();
            // work on task for that topic

            log.info("coming here topic {}", topic);
            log.info("coming here case {}", ccdId);
            log.info("coming here event {}", eventId);
            coreCaseDataService.triggerEvent(Long.valueOf(ccdId), CaseEvent.valueOf(eventId));
//                if (success) {
            externalTaskService.complete(externalTask, new HashMap<>(variables));
//                } else {
//                    // if the work was not successful, mark it as failed
//                    externalTaskService.handleFailure(
//                        task.getId(),
//                        "externalWorkerId",
//                        "Address could not be validated: Address database not reachable",
//                        1, 10L * 60L * 1000L
//                    );
//                }
        } catch (Exception e) {
            //... handle exception
        }

    }
}
