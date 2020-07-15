package uk.gov.hmcts.reform.ucmc.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.ucmc.event.MoveCaseToStayedEvent;
import uk.gov.hmcts.reform.ucmc.service.CoreCaseDataService;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MoveCaseToStayedEventHandler {
    private final CoreCaseDataService coreCaseDataService;

    @EventListener
    public void moveCaseToStayed(MoveCaseToStayedEvent event) {
        coreCaseDataService.triggerEvent(event.getCaseId(), "MOVE_TO_STAYED", event.getData());
    }
}
