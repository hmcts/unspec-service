package uk.gov.hmcts.reform.ucmc.event;

import lombok.Value;

import java.util.Map;

@Value
public class MoveCaseToStayedEvent {
    Long caseId;
    Map<String, Object> data;
}
