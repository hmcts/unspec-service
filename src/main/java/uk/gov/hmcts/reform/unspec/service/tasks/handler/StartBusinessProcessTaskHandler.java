package uk.gov.hmcts.reform.unspec.service.tasks.handler;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.ccd.client.model.CaseDataContent;
import uk.gov.hmcts.reform.ccd.client.model.Event;
import uk.gov.hmcts.reform.ccd.client.model.StartEventResponse;
import uk.gov.hmcts.reform.unspec.callback.CaseEvent;
import uk.gov.hmcts.reform.unspec.helpers.CaseDetailsConverter;
import uk.gov.hmcts.reform.unspec.model.BusinessProcess;
import uk.gov.hmcts.reform.unspec.model.CaseData;
import uk.gov.hmcts.reform.unspec.service.CoreCaseDataService;
import uk.gov.hmcts.reform.unspec.service.flowstate.StateFlowEngine;
import uk.gov.hmcts.reform.unspec.stateflow.StateFlow;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class StartBusinessProcessTaskHandler implements ExternalTaskHandler {

    private final CoreCaseDataService coreCaseDataService;
    private final CaseDetailsConverter caseDetailsConverter;
    private final StateFlowEngine stateFlowEngine;

    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        Map<String, Object> allVariables = externalTask.getAllVariables();
        String ccdId = (String) allVariables.get("CCD_ID");
        CaseEvent caseEvent = CaseEvent.valueOf((String) allVariables.get("CASE_EVENT"));
        CaseData caseData = startBusinessProcess(ccdId, caseEvent, externalTask);

        StateFlow stateFlow = stateFlowEngine.evaluate(caseData);
        VariableMap variables = Variables.createVariables();
        variables.putValue("flowState", stateFlow.getState().getName());

        externalTaskService.complete(externalTask, variables);
    }

    private CaseData startBusinessProcess(String ccdId, CaseEvent caseEvent, ExternalTask externalTask) {
        StartEventResponse startEventResponse = coreCaseDataService.startUpdate(ccdId, caseEvent);

        CaseData data = caseDetailsConverter.toCaseData(startEventResponse.getCaseDetails());
        BusinessProcess businessProcess = data.getBusinessProcess();

        switch (businessProcess.getStatusOrDefault()) {
            case READY:
            case DISPATCHED:
                return updateBusinessProcess(ccdId, externalTask, startEventResponse, businessProcess);
            case STARTED:
                if (businessProcess.hasSimilarProcessInstanceId(externalTask.getProcessInstanceId())) {
                    throw new BpmnError("ABORT");
                }
                return data;
            default:
                throw new BpmnError("ABORT");
        }
    }

    private CaseData updateBusinessProcess(
        String ccdId,
        ExternalTask externalTask,
        StartEventResponse startEventResponse,
        BusinessProcess businessProcess
    ) {
        businessProcess = businessProcess.toBuilder().processInstanceId(externalTask.getProcessInstanceId()).build();

        return coreCaseDataService.submitUpdate(ccdId, caseDataContent(startEventResponse, businessProcess));
    }

    private CaseDataContent caseDataContent(StartEventResponse startEventResponse, BusinessProcess businessProcess) {
        Map<String, Object> data = startEventResponse.getCaseDetails().getData();
        data.put("businessProcess", businessProcess);

        return CaseDataContent.builder()
            .eventToken(startEventResponse.getToken())
            .event(Event.builder().id(startEventResponse.getEventId()).build())
            .data(data)
            .build();
    }
}
