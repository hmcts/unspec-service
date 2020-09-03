package uk.gov.hmcts.reform.unspec.camunda.tasks;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.client.topic.TopicSubscription;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.unspec.service.CoreCaseDataService;


@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationTask {

    private final RuntimeService runtimeService;
    private final RepositoryService repositoryService;
    private final CoreCaseDataService coreCaseDataService;

    @ExternalTaskSubscription("")
    public void execute(ExternalTask externalTask) {

        coreCaseDataService.triggerEvent("caseId", CaseEvent.NOTIFY_);

    }
}
