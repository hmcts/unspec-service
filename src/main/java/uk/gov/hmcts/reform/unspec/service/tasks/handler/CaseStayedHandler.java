package uk.gov.hmcts.reform.unspec.service.tasks.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.unspec.event.MoveCaseToStayedEvent;
import uk.gov.hmcts.reform.unspec.service.search.CaseStayedSearchService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class CaseStayedHandler implements ExternalTaskHandler {

    private final CaseStayedSearchService caseSearchService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        final String taskName = externalTask.getTopicName();

        log.info("Job {} started", taskName);

        List<CaseDetails> cases = caseSearchService.getCases();

        log.info("Job '{}' found {} case(s)", taskName, cases.size());

        cases.forEach(caseDetails -> applicationEventPublisher.publishEvent(
            new MoveCaseToStayedEvent(caseDetails.getId())));

        externalTaskService.complete(externalTask);

        log.info("Job '{}' finished", taskName);
    }
}
