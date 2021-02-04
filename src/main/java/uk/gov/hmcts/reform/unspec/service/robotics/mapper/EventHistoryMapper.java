package uk.gov.hmcts.reform.unspec.service.robotics.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.unspec.model.CaseData;
import uk.gov.hmcts.reform.unspec.model.robotics.Event;
import uk.gov.hmcts.reform.unspec.model.robotics.EventDetails;
import uk.gov.hmcts.reform.unspec.model.robotics.EventHistory;
import uk.gov.hmcts.reform.unspec.service.flowstate.FlowState;
import uk.gov.hmcts.reform.unspec.service.flowstate.StateFlowEngine;
import uk.gov.hmcts.reform.unspec.stateflow.model.State;

import java.util.List;

import static java.time.format.DateTimeFormatter.ISO_DATE;

@Component
@RequiredArgsConstructor
public class EventHistoryMapper {

    private final StateFlowEngine stateFlowEngine;

    @SuppressWarnings("MissingSwitchDefault")
    public EventHistory buildEvents(CaseData caseData) {
        EventHistory.EventHistoryBuilder builder = EventHistory.builder();
        State state = stateFlowEngine.evaluate(caseData).getState();
        FlowState.Main mainFlowState = (FlowState.Main) FlowState.fromFullName(state.getName());
        switch (mainFlowState) {
            case PROCEEDS_OFFLINE_UNREPRESENTED_DEFENDANT:
                buildUnrepresentedDefendant(caseData, builder);
                break;
        }
        return builder.build();
    }

    private void buildUnrepresentedDefendant(CaseData caseData, EventHistory.EventHistoryBuilder builder) {
        builder.miscellaneous(
            List.of(Event.builder()
                        .eventSequence(1)
                        .eventCode("999")
                        .dateReceived(caseData.getClaimSubmittedDateTime().toLocalDate().format(ISO_DATE))
                        .eventDetails(EventDetails.builder()
                                          .miscText("RPA Reason: Unrepresented defendant.")
                                          .build())
                        .build()
            ));
    }
}
