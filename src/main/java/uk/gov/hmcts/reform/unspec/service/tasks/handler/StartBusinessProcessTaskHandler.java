package uk.gov.hmcts.reform.unspec.service.tasks.handler;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.ccd.client.model.CaseDataContent;
import uk.gov.hmcts.reform.ccd.client.model.Event;
import uk.gov.hmcts.reform.ccd.client.model.StartEventResponse;
import uk.gov.hmcts.reform.unspec.callback.CaseEvent;
import uk.gov.hmcts.reform.unspec.helpers.CaseDetailsConverter;
import uk.gov.hmcts.reform.unspec.model.BusinessProcess;
import uk.gov.hmcts.reform.unspec.model.BusinessProcessStatus;
import uk.gov.hmcts.reform.unspec.model.CaseData;
import uk.gov.hmcts.reform.unspec.service.CoreCaseDataService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class StartBusinessProcessTaskHandler implements ExternalTaskHandler {

    private final CoreCaseDataService coreCaseDataService;
    private final CaseDetailsConverter caseDetailsConverter;

    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        Map<String, Object> allVariables = externalTask.getAllVariables();
        String ccdId = (String) allVariables.get("CCD_ID");
        CaseEvent caseEvent = CaseEvent.valueOf((String) allVariables.get("CASE_EVENT"));
        startBusinessProcess(ccdId, caseEvent, externalTask);
        externalTaskService.complete(externalTask);
    }

    private void startBusinessProcess(String ccdId, CaseEvent caseEvent, ExternalTask externalTask) {
        StartEventResponse startEventResponse = coreCaseDataService.startUpdate(ccdId, caseEvent);
        CaseData data = caseDetailsConverter.toCaseData(startEventResponse.getCaseDetails());
        BusinessProcess businessProcess = data.getBusinessProcess();

        switch (getStatus(businessProcess)) {
            case READY:
            case DISPATCHED:
                updateBusinessProcess(ccdId, externalTask, startEventResponse, data, businessProcess);
                break;
            case STARTED:
                if (externalTask.getProcessInstanceId().equals(businessProcess.getProcessInstanceId())) {
                    throw new BpmnError("ABORT");
                }
                break;
            default:
                throw new BpmnError("ABORT");
        }
    }

    private void updateBusinessProcess(
        String ccdId,
        ExternalTask externalTask,
        StartEventResponse startEventResponse,
        CaseData data,
        BusinessProcess businessProcess
    ) {
        businessProcess = businessProcess.toBuilder()
            .processInstanceId(externalTask.getProcessInstanceId())
            .build();

        coreCaseDataService.submitUpdate(ccdId, caseDataContent(startEventResponse, businessProcess));
    }

    private BusinessProcessStatus getStatus(BusinessProcess businessProcess) {
        return Optional.ofNullable(businessProcess.getStatus()).orElse(BusinessProcessStatus.READY);
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
