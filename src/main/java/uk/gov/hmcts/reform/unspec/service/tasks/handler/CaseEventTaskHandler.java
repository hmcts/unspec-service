package uk.gov.hmcts.reform.unspec.service.tasks.handler;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.ccd.client.model.CaseDataContent;
import uk.gov.hmcts.reform.ccd.client.model.Event;
import uk.gov.hmcts.reform.ccd.client.model.StartEventResponse;
import uk.gov.hmcts.reform.unspec.callback.CaseEvent;
import uk.gov.hmcts.reform.unspec.helpers.CaseDetailsConverter;
import uk.gov.hmcts.reform.unspec.model.BusinessProcess;
import uk.gov.hmcts.reform.unspec.model.CaseData;
import uk.gov.hmcts.reform.unspec.model.ExternalTaskInput;
import uk.gov.hmcts.reform.unspec.service.CoreCaseDataService;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class CaseEventTaskHandler implements ExternalTaskHandler {

    private final CoreCaseDataService coreCaseDataService;
    private final CaseDetailsConverter caseDetailsConverter;

    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        Map<String, Object> allVariables = externalTask.getAllVariables();
        ExternalTaskInput externalTaskInput = caseDetailsConverter.fromMap(allVariables, ExternalTaskInput.class);
        updateBusinessProcessActivityId(externalTask, externalTaskInput.getCaseId(), externalTaskInput.getCaseEvent());
        externalTaskService.complete(externalTask);
    }

    private void updateBusinessProcessActivityId(ExternalTask externalTask, String ccdId, CaseEvent caseEvent) {
        StartEventResponse startEventResponse = coreCaseDataService.startUpdate(ccdId, caseEvent);
        CaseData data = caseDetailsConverter.toCaseData(startEventResponse.getCaseDetails());
        BusinessProcess businessProcess = data.getBusinessProcess().toBuilder()
            .activityId(externalTask.getActivityId())
            .build();

        coreCaseDataService.submitUpdate(ccdId, caseDataContent(startEventResponse, businessProcess));
    }

    private CaseDataContent caseDataContent(
        StartEventResponse startEventResponse,
        BusinessProcess businessProcess
    ) {
        HashMap<String, Object> data = new HashMap<>(startEventResponse.getCaseDetails().getData());
        data.put("businessProcess", businessProcess);

        return CaseDataContent.builder()
            .eventToken(startEventResponse.getToken())
            .event(Event.builder().id(startEventResponse.getEventId()).build())
            .data(data)
            .build();
    }
}
