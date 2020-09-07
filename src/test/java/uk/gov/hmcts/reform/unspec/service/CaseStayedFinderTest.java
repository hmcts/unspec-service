package uk.gov.hmcts.reform.unspec.service;

import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.unspec.event.MoveCaseToStayedEvent;
import uk.gov.hmcts.reform.unspec.service.search.CaseStayedSearchService;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class CaseStayedFinderTest {

    @Mock
    private ExternalTask externalTask;

    @Mock
    private ExternalTaskService service;

    @Mock
    private CaseStayedSearchService searchService;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private CaseStayedFinder caseStayedFinder;

    @BeforeEach
    void init() {
        when(externalTask.getTopicName()).thenReturn("test");
        when(externalTask.getWorkerId()).thenReturn("worker");
    }

    @Test
    void shouldEmitMoveCaseToStayedEvent_whenCasesFound() {
        long caseId = 1L;
        Map<String, Object> data = Map.of("data", "some data");
        List<CaseDetails> caseDetails = List.of(CaseDetails.builder()
                                                    .id(caseId)
                                                    .data(data)
                                                    .build());

        when(searchService.getCases()).thenReturn(caseDetails);

        caseStayedFinder.execute(externalTask, service);

        verify(applicationEventPublisher).publishEvent(new MoveCaseToStayedEvent(caseId));
        verify(service).complete(externalTask);
    }

    @Test
    void shouldNotEmitMoveCaseToStayedEvent_WhenNoCasesFound() {
        when(searchService.getCases()).thenReturn(List.of());

        caseStayedFinder.execute(externalTask, service);

        verifyNoInteractions(applicationEventPublisher);
    }

    @Test
    void shouldCatchError_whenException() {
        String errorMessage = "there was an error";

        when(searchService.getCases()).thenAnswer(invocation -> {
            throw new Exception(errorMessage);
        });

        caseStayedFinder.execute(externalTask, service);

        verify(service).handleFailure(externalTask, "worker", errorMessage, 0, 0L);
    }
}
