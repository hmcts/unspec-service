package uk.gov.hmcts.reform.unspec.service.tasks.handler;

import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static uk.gov.hmcts.reform.unspec.helpers.ExponentialRetryTimeoutHelper.calculateExponentialRetryTimeout;

public interface BaseExternalTaskHandler extends ExternalTaskHandler {

    Logger log = LoggerFactory.getLogger(BaseExternalTaskHandler.class);

    @Override
    default void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        try {
            log.info("External task '{}' started", externalTask.getTopicName());
            handleTask(externalTask);
        } catch (Exception e) {
            handleFailure(externalTask, externalTaskService, e);
            log.error("External task '{}' errored due to {}", externalTask.getTopicName(), e.getMessage());
        }
        try {
            externalTaskService.complete(externalTask);
            log.info("External task '{}' finished", externalTask.getTopicName());
        } catch (Exception e) {
            log.error("Completing external task '{}' errored due to {}", externalTask.getTopicName(), e.getMessage());
        }
    }

    private void handleFailure(ExternalTask externalTask, ExternalTaskService externalTaskService, Exception e) {
        int remainingRetries = externalTask.getRetries() == null ? getMaxRetries() : externalTask.getRetries();

        externalTaskService.handleFailure(
            externalTask,
            externalTask.getWorkerId(),
            e.getMessage(),
            remainingRetries - 1,
            calculateExponentialRetryTimeout(500, getMaxRetries(), remainingRetries)
        );
    }

    void handleTask(ExternalTask externalTask);

    int getMaxRetries();
}
