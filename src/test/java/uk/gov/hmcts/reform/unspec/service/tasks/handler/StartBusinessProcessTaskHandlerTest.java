package uk.gov.hmcts.reform.unspec.service.tasks.handler;

import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.ccd.client.model.CaseDataContent;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.ccd.client.model.StartEventResponse;
import uk.gov.hmcts.reform.unspec.enums.BusinessProcessStatus;
import uk.gov.hmcts.reform.unspec.helpers.CaseDetailsConverter;
import uk.gov.hmcts.reform.unspec.model.BusinessProcess;
import uk.gov.hmcts.reform.unspec.sampledata.CaseDataBuilder;
import uk.gov.hmcts.reform.unspec.sampledata.CaseDetailsBuilder;
import uk.gov.hmcts.reform.unspec.service.CoreCaseDataService;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.unspec.callback.CaseEvent.START_BUSINESS_PROCESS;

@SpringBootTest(classes = {
    StartBusinessProcessTaskHandler.class,
    JacksonAutoConfiguration.class,
    CaseDetailsConverter.class
})
@ExtendWith(SpringExtension.class)
class StartBusinessProcessTaskHandlerTest {

    private static final Long CASE_ID = 1L;
    public static final String PROCESS_INSTANCE_ID = "processInstanceId";

    @Mock
    private ExternalTask mockExternalTask;
    @Mock
    private ExternalTaskService externalTaskService;
    @MockBean
    private CoreCaseDataService coreCaseDataService;
    @Autowired
    private StartBusinessProcessTaskHandler startBusinessProcessTaskHandler;

    @BeforeEach
    void init() {
        when(mockExternalTask.getTopicName()).thenReturn("test");
        when(mockExternalTask.getWorkerId()).thenReturn("worker");
        when(mockExternalTask.getActivityId()).thenReturn("activityId");
        when(mockExternalTask.getProcessInstanceId()).thenReturn(PROCESS_INSTANCE_ID);
        when(mockExternalTask.getAllVariables())
            .thenReturn(Map.of(
                "CCD_ID",
                CASE_ID.toString(),
                "CASE_EVENT",
                START_BUSINESS_PROCESS.getValue()
            ));
    }

    @Nested
    class SuccessHandler {

        @Test
        void shouldUpdateBusinessStatusToSTARTED_whenInputStatusIsREADY() {
            CaseDetails caseDetails = CaseDetailsBuilder.builder()
                .data(new CaseDataBuilder().atStateClaimDraft().build().toBuilder()
                          .businessProcess(BusinessProcess.builder().status(BusinessProcessStatus.READY).build())
                          .build())
                .build();

            when(coreCaseDataService.startUpdate(eq(CASE_ID.toString()), eq(START_BUSINESS_PROCESS)))
                .thenReturn(StartEventResponse.builder().caseDetails(caseDetails).build());

            doNothing().when(coreCaseDataService).submitUpdate(eq(CASE_ID.toString()), any(CaseDataContent.class));

            startBusinessProcessTaskHandler.execute(mockExternalTask, externalTaskService);

            verify(coreCaseDataService, atLeast(2)).startUpdate(eq(CASE_ID.toString()), eq(START_BUSINESS_PROCESS));
            verify(coreCaseDataService).submitUpdate(eq(CASE_ID.toString()), any(CaseDataContent.class));
            verify(externalTaskService).complete(mockExternalTask);
        }

        @Test
        void shouldUpdateBusinessStatusToSTARTED_whenInputStatusIsDISPATCHED() {
            CaseDetails caseDetails = CaseDetailsBuilder.builder()
                .data(new CaseDataBuilder().atStateClaimDraft().build().toBuilder()
                          .businessProcess(BusinessProcess.builder().status(BusinessProcessStatus.DISPATCHED).build())
                          .build())
                .build();

            when(coreCaseDataService.startUpdate(eq(CASE_ID.toString()), eq(START_BUSINESS_PROCESS)))
                .thenReturn(StartEventResponse.builder().caseDetails(caseDetails).build());

            doNothing().when(coreCaseDataService).submitUpdate(eq(CASE_ID.toString()), any(CaseDataContent.class));

            startBusinessProcessTaskHandler.execute(mockExternalTask, externalTaskService);

            verify(coreCaseDataService, atLeast(3)).startUpdate(eq(CASE_ID.toString()), eq(START_BUSINESS_PROCESS));
            verify(coreCaseDataService, atLeast(2)).submitUpdate(eq(CASE_ID.toString()), any(CaseDataContent.class));
            verify(externalTaskService).complete(mockExternalTask);
        }
    }

    @Nested
    class FailureHandler {

        @Test
        void shouldRaiseBPMNError_whenStatusExecutedIsStarted() {
            CaseDetails caseDetails = CaseDetailsBuilder.builder()
                .data(new CaseDataBuilder().atStateClaimDraft().build().toBuilder()
                          .businessProcess(BusinessProcess.builder()
                                               .status(BusinessProcessStatus.STARTED)
                                               .processInstanceId(PROCESS_INSTANCE_ID)
                                               .build())
                          .build())
                .build();

            when(coreCaseDataService.startUpdate(eq(CASE_ID.toString()), eq(START_BUSINESS_PROCESS)))
                .thenReturn(StartEventResponse.builder().caseDetails(caseDetails).build());

            assertThrows(
                BpmnError.class,
                () -> startBusinessProcessTaskHandler.execute(mockExternalTask, externalTaskService),
                "ABORT"
            );

            verify(coreCaseDataService).startUpdate(eq(CASE_ID.toString()), eq(START_BUSINESS_PROCESS));
        }
    }

}
