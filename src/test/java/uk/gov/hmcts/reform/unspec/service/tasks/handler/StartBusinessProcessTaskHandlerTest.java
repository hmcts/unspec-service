package uk.gov.hmcts.reform.unspec.service.tasks.handler;

import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.variable.VariableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
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
import uk.gov.hmcts.reform.unspec.model.CaseData;
import uk.gov.hmcts.reform.unspec.sampledata.CaseDataBuilder;
import uk.gov.hmcts.reform.unspec.sampledata.CaseDetailsBuilder;
import uk.gov.hmcts.reform.unspec.service.CoreCaseDataService;
import uk.gov.hmcts.reform.unspec.service.flowstate.StateFlowEngine;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.unspec.callback.CaseEvent.START_BUSINESS_PROCESS;
import static uk.gov.hmcts.reform.unspec.enums.BusinessProcessStatus.STARTED;

@SpringBootTest(classes = {
    StartBusinessProcessTaskHandler.class,
    JacksonAutoConfiguration.class,
    CaseDetailsConverter.class,
    StateFlowEngine.class
})
@ExtendWith(SpringExtension.class)
class StartBusinessProcessTaskHandlerTest {

    private static final String CASE_ID = "1";
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

        when(mockExternalTask.getAllVariables()).thenReturn(Map.of(
            "caseId", CASE_ID,
            "caseEvent", START_BUSINESS_PROCESS.name()
        ));
    }

    @ParameterizedTest
    @EnumSource(value = BusinessProcessStatus.class, names = {"READY", "DISPATCHED"})
    void shouldCompleteExternalTaskService_whenInputStatusIs(BusinessProcessStatus status) {
        CaseData caseData = new CaseDataBuilder().atStateClaimDraft()
            .businessProcess(BusinessProcess.builder().status(status).build())
            .build();

        CaseDetails caseDetails = CaseDetailsBuilder.builder().data(caseData).build();

        when(coreCaseDataService.startUpdate(eq(CASE_ID), eq(START_BUSINESS_PROCESS)))
            .thenReturn(StartEventResponse.builder().caseDetails(caseDetails).build());

        when(coreCaseDataService.submitUpdate(eq(CASE_ID), any(CaseDataContent.class))).thenReturn(caseData);

        startBusinessProcessTaskHandler.execute(mockExternalTask, externalTaskService);

        verify(coreCaseDataService).startUpdate(eq(CASE_ID), eq(START_BUSINESS_PROCESS));
        verify(coreCaseDataService).submitUpdate(eq(CASE_ID), any(CaseDataContent.class));
        verify(externalTaskService).complete(eq(mockExternalTask), any(VariableMap.class));
    }

    @Test
    void shouldCompleteExternalTaskService_whenInputStatusIsStartedAndHaveDifferentProcessInstanceId() {
        CaseData caseData = new CaseDataBuilder().atStateClaimDraft()
            .businessProcess(BusinessProcess.builder().status(STARTED).processInstanceId("differentId").build())
            .build();

        CaseDetails caseDetails = CaseDetailsBuilder.builder().data(caseData).build();

        when(coreCaseDataService.startUpdate(eq(CASE_ID), eq(START_BUSINESS_PROCESS)))
            .thenReturn(StartEventResponse.builder().caseDetails(caseDetails).build());

        when(coreCaseDataService.submitUpdate(eq(CASE_ID), any(CaseDataContent.class))).thenReturn(caseData);

        startBusinessProcessTaskHandler.execute(mockExternalTask, externalTaskService);

        verify(coreCaseDataService).startUpdate(eq(CASE_ID), eq(START_BUSINESS_PROCESS));
        verify(externalTaskService).complete(eq(mockExternalTask), any(VariableMap.class));
    }

    @ParameterizedTest
    @EnumSource(value = BusinessProcessStatus.class, names = {"STARTED", "FINISHED"})
    void shouldRaiseBpmnError_whenStatusExecutedIsStarted(BusinessProcessStatus status) {
        CaseData caseData = new CaseDataBuilder().atStateClaimDraft()
            .businessProcess(BusinessProcess.builder().status(status).processInstanceId(PROCESS_INSTANCE_ID).build())
            .build();

        CaseDetails caseDetails = CaseDetailsBuilder.builder().data(caseData).build();

        when(coreCaseDataService.startUpdate(eq(CASE_ID), eq(START_BUSINESS_PROCESS)))
            .thenReturn(StartEventResponse.builder().caseDetails(caseDetails).build());

        assertThrows(
            BpmnError.class,
            () -> startBusinessProcessTaskHandler.execute(mockExternalTask, externalTaskService),
            "ABORT"
        );

        verify(coreCaseDataService).startUpdate(eq(CASE_ID), eq(START_BUSINESS_PROCESS));
    }
}
