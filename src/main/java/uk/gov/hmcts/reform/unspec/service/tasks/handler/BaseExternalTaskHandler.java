package uk.gov.hmcts.reform.unspec.service.tasks.handler;

import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.variable.VariableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Optional.ofNullable;
import static uk.gov.hmcts.reform.unspec.helpers.ExponentialRetryTimeoutHelper.calculateExponentialRetryTimeout;

/**
 * Interface for standard implementation of task handler that is invoked for each fetched and locked task.
 */
public interface BaseExternalTaskHandler extends ExternalTaskHandler {

    Logger log = LoggerFactory.getLogger(BaseExternalTaskHandler.class);

    /**
     * Executed for each fetched and locked task.
     *
     * @param externalTask        the context is represented of.
     * @param externalTaskService to interact with fetched and locked tasks.
     */
    @Override
    default void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        String topicName = externalTask.getTopicName();

        try {
            log.info("External task '{}' started", topicName);
            handleTask(externalTask);
            try {
                ofNullable(getVariableMap()).ifPresentOrElse(
                    variableMap -> externalTaskService.complete(externalTask, variableMap),
                    () -> externalTaskService.complete(externalTask)
                );
                log.info("External task '{}' finished", topicName);
            } catch (Exception e) {
                log.error("Completing external task '{}' errored due to {}", topicName, e);
            }
        } catch (BpmnError e) {
            externalTaskService.handleBpmnError(externalTask, e.getErrorCode());
            log.error("Bpmn error for external task '{}' due to {}", topicName, e);
        } catch (Exception e) {
            handleFailure(externalTask, externalTaskService, e);
            log.error("External task '{}' errored due to {}", topicName, e);
        }
    }

    /**
     * Called when an exception arises from the {@link BaseExternalTaskHandler handleTask(externalTask)} method.
     *
     * @param externalTask        the external task to be handled.
     * @param externalTaskService to interact with fetched and locked tasks.
     * @param e                   the exception thrown by business logic.
     */
    default void handleFailure(ExternalTask externalTask, ExternalTaskService externalTaskService, Exception e) {
        int maxRetries = getMaxAttempts();
        int remainingRetries = externalTask.getRetries() == null ? maxRetries : externalTask.getRetries();

        externalTaskService.handleFailure(
            externalTask,
            externalTask.getWorkerId(),
            e.getMessage(),
            remainingRetries - 1,
            calculateExponentialRetryTimeout(500, maxRetries, remainingRetries)
        );
    }

    /**
     * Defines the number of attempts for a given external task.
     *
     * @return the number of attempts for an external task.
     */
    default int getMaxAttempts() {
        return 3;
    }

    /**
     * Defines a Map of variables to be added to an external task on completion.
     * By default this is null, override to add values.
     *
     * @return the variables to add to the external task.
     */
    default VariableMap getVariableMap() {
        return null;
    }

    /**
     * Executed for each fetched and locked task.
     *
     * @param externalTask the external task to be handled.
     */
    void handleTask(ExternalTask externalTask);
}
