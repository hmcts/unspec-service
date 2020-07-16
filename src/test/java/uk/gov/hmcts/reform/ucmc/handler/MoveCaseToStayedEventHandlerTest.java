package uk.gov.hmcts.reform.ucmc.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.ucmc.event.MoveCaseToStayedEvent;
import uk.gov.hmcts.reform.ucmc.service.CoreCaseDataService;

import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class MoveCaseToStayedEventHandlerTest {

    @Mock
    private CoreCaseDataService coreCaseDataService;

    @InjectMocks
    private MoveCaseToStayedEventHandler handler;

    @Test
    void shouldTriggerMoveToStayedEvent() {
        MoveCaseToStayedEvent event = new MoveCaseToStayedEvent(1L);

        handler.moveCaseToStayed(event);

        verify(coreCaseDataService).triggerEvent(event.getCaseId(), "MOVE_TO_STAYED");
    }
}
