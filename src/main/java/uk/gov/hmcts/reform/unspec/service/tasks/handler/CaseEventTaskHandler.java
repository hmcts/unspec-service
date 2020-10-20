package uk.gov.hmcts.reform.unspec.service.tasks.handler;

import feign.FeignException;
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

@Component
@Slf4j
@RequiredArgsConstructor
public class CaseEventTaskHandler implements ExternalTaskHandler {

    private final CoreCaseDataService coreCaseDataService;

    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        Map<String, Object> allVariables = externalTask.getAllVariables();
        Long ccdId = (Long) allVariables.get("CCD_ID");
        String eventId = (String) allVariables.get("CASE_EVENT");

        try {
            coreCaseDataService.triggerEvent(
                ccdId,
                CaseEvent.valueOf(eventId),
                Map.of(
                    "businessProcess",
                    BusinessProcess.builder().activityId(externalTask.getActivityId()).build()
                )
            );
        } catch (FeignException e) {
            log.error(e.contentUTF8());
            throw e;
        }
        externalTaskService.complete(externalTask);
    }
}
