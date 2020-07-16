package uk.gov.hmcts.reform.ucmc.event;

import lombok.Value;

@Value
public class MoveCaseToStayedEvent {
    Long caseId;
}
