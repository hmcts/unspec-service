package uk.gov.hmcts.reform.unspec.service.tasks.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.unspec.helpers.CaseDetailsConverter;
import uk.gov.hmcts.reform.unspec.service.search.CaseReadySearchService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class CaseReadyHandler implements ExternalTaskHandler {

    private final CaseReadySearchService caseSearchService;
    private final CaseDetailsConverter caseDetailsConverter;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        final String taskName = externalTask.getTopicName();
        log.info("Job {} started", taskName);

        List<CaseDetails> cases = caseSearchService.getCases();
        log.info("Job '{}' found {} case(s)", taskName, cases.size());
        cases.forEach(caseDetails -> {
            var caseData = caseDetailsConverter.toCaseData(caseDetails);
            var id = caseDetails.getId();
            var businessProcess = caseData.getBusinessProcess();
            //TODO: emit BusinessProcess.event to Camunda -> trigger Message Start Event via API
            //TODO: check response
            //TODO: failed -> log and move on
            //TODO: successful -> publish following CCD event:
            //applicationEventPublisher.publishEvent(new BusinessProcessDispatchedEvent(caseDetails.getId())));
            //TODO: in event handler -> move status to DISPATCHED if hasn't already progressed
        });

        externalTaskService.complete(externalTask);
        log.info("Job '{}' finished", taskName);
    }
}
