package uk.gov.hmcts.reform.unspec.model.robotics;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EventHistory {

    private List<Event> miscellaneous;
    private List acknowledgementOfServiceReceived;
    private List consentExtensionFilingDefence;
    private List defenceFiled;
    private List defenceAndCounterClaim;
    private List receiptOfPartAdmission;
    private List receiptofAdmission;
    private List replyToDefence;
    private List directionsQuestionnaireFiled;
}
