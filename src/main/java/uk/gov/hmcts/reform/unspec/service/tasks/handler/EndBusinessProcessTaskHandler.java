package uk.gov.hmcts.reform.unspec.service.tasks.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.ccd.client.model.CaseDataContent;
import uk.gov.hmcts.reform.ccd.client.model.Event;
import uk.gov.hmcts.reform.ccd.client.model.StartEventResponse;
import uk.gov.hmcts.reform.unspec.helpers.CaseDetailsConverter;
import uk.gov.hmcts.reform.unspec.model.BusinessProcess;
import uk.gov.hmcts.reform.unspec.model.CaseData;
import uk.gov.hmcts.reform.unspec.model.ExternalTaskInput;
import uk.gov.hmcts.reform.unspec.service.CoreCaseDataService;

import java.util.Map;

import static uk.gov.hmcts.reform.unspec.callback.CaseEvent.END_BUSINESS_PROCESS;
import static uk.gov.hmcts.reform.unspec.helpers.ExponentialRetryTimeoutHelper.calculateExponentialRetryTimeout;

@Slf4j
@Component
@RequiredArgsConstructor
public class EndBusinessProcessTaskHandler implements ExternalTaskHandler {

    private final CoreCaseDataService coreCaseDataService;
    private final CaseDetailsConverter caseDetailsConverter;

    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        Map<String, Object> allVariables = externalTask.getAllVariables();
        ExternalTaskInput externalTaskInput = caseDetailsConverter.fromMap(allVariables, ExternalTaskInput.class);
        String caseId = externalTaskInput.getCaseId();

        log.info("Ending business process on case: {}", externalTaskInput.getCaseId());

        try {
            StartEventResponse startEventResponse = coreCaseDataService.startUpdate(caseId, END_BUSINESS_PROCESS);

            CaseData data = caseDetailsConverter.toCaseData(startEventResponse.getCaseDetails());
            BusinessProcess businessProcess = data.getBusinessProcess();

            coreCaseDataService.submitUpdate(caseId, caseDataContent(startEventResponse, businessProcess));
            externalTaskService.complete(externalTask);

            log.info("Finished business process on case: {}", externalTaskInput.getCaseId());

        } catch (Exception e) {
            int maxRetries = 3;
            int remainingRetries = externalTask.getRetries() == null ? maxRetries : externalTask.getRetries();

            externalTaskService.handleFailure(
                externalTask,
                externalTask.getWorkerId(),
                e.getMessage(),
                remainingRetries - 1,
                calculateExponentialRetryTimeout(500, maxRetries, remainingRetries)
            );

            log.error(
                "Failed to end business process on case: {}.\n Reason for failure: {}.\n Remaining retries: {}",
                externalTaskInput.getCaseId(),
                e.getMessage(),
                remainingRetries - 1
            );
        }
    }

    private CaseDataContent caseDataContent(StartEventResponse startEventResponse, BusinessProcess businessProcess) {
        Map<String, Object> data = startEventResponse.getCaseDetails().getData();
        data.put("businessProcess", businessProcess.reset());

        return CaseDataContent.builder()
            .eventToken(startEventResponse.getToken())
            .event(Event.builder().id(startEventResponse.getEventId()).build())
            .data(data)
            .build();
    }
}
