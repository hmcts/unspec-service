package uk.gov.hmcts.reform.unspec.model.robotics;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class Event {

    private Integer eventSequence;
    private String eventCode;
    private String dateReceived;
    private String litigiousPartyID;
    private EventDetails eventDetails;
}
