package uk.gov.hmcts.reform.unspec.service.tasks.handler;

import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.unspec.service.CoreCaseDataService;

import java.util.Map;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.unspec.callback.CaseEvent.MAKE_PBA_PAYMENT;

@ExtendWith(SpringExtension.class)
class PaymentTaskHandlerTest {

    private static final Long CASE_ID = 1L;

    @Mock
    private ExternalTask mockExternalTask;
    @Mock
    private ExternalTaskService externalTaskService;
    @Mock
    private CoreCaseDataService coreCaseDataService;
    @InjectMocks
    private PaymentTaskHandler paymentTaskHandler;

    @BeforeEach
    void init() {
        when(mockExternalTask.getTopicName()).thenReturn("test");
        when(mockExternalTask.getWorkerId()).thenReturn("worker");
        when(mockExternalTask.getActivityId()).thenReturn("activityId");
        when(mockExternalTask.getAllVariables()).thenReturn(Map.of("CCD_ID", CASE_ID));
    }

    @Nested
    class SuccessHandler {

        @Test
        void shouldTriggerMakePbaPaymentCCDEvent_whenHandlerIsExecuted() {
            paymentTaskHandler.execute(mockExternalTask, externalTaskService);

            verify(coreCaseDataService).triggerEvent(
                eq(CASE_ID),
                eq(MAKE_PBA_PAYMENT),
                anyMap()
            );
            verify(externalTaskService).complete(mockExternalTask);
        }
    }
}
