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
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.PROCEEDS_OFFLINE_UNREPRESENTED_DEFENDANT;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.RESPONDENT_COUNTER_CLAIM;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.RESPONDENT_FULL_ADMISSION;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.RESPONDENT_PART_ADMISSION;

@Component
@RequiredArgsConstructor
public class EventHistoryMapper {

    private static final List<FlowState.Main> DEFENDANT_RESPONSE_STATE_FLOWS = List.of(
        RESPONDENT_FULL_ADMISSION,
        RESPONDENT_PART_ADMISSION,
        RESPONDENT_COUNTER_CLAIM
    );

    private final StateFlowEngine stateFlowEngine;

    public EventHistory buildEvents(CaseData caseData) {
        EventHistory.EventHistoryBuilder builder = EventHistory.builder();
        State state = stateFlowEngine.evaluate(caseData).getState();
        FlowState.Main mainFlowState = (FlowState.Main) FlowState.fromFullName(state.getName());
        if (DEFENDANT_RESPONSE_STATE_FLOWS.contains(mainFlowState)) {
            buildDefendantResponse(mainFlowState, caseData, builder);
        }
        if (mainFlowState == PROCEEDS_OFFLINE_UNREPRESENTED_DEFENDANT) {
            buildUnrepresentedDefendant(caseData, builder);
        }
        return builder.build();
    }

    private void buildUnrepresentedDefendant(CaseData caseData, EventHistory.EventHistoryBuilder builder) {
        builder.miscellaneous(
            List.of(
                Event.builder()
                    .eventSequence(1)
                    .eventCode("999")
                    .dateReceived(caseData.getClaimSubmittedDateTime().toLocalDate().format(ISO_DATE))
                    .eventDetails(EventDetails.builder()
                                      .miscText("RPA Reason: Unrepresented defendant.")
                                      .build())
                    .build()
            ));
    }

    private void buildDefendantResponse(FlowState.Main mainFlowState, CaseData caseData, EventHistory.EventHistoryBuilder builder) {
        String rpaReason;
        switch (mainFlowState) {
            case RESPONDENT_FULL_ADMISSION:
                rpaReason = "Defendant fully admits.";
                builder.receiptOfAdmission(
                    List.of(
                        Event.builder()
                            .eventSequence(4)
                            .eventCode("40")
                            .dateReceived("TODO")
                            .litigiousPartyID("002")
                            .build()
                    )
                );
                break;
            case RESPONDENT_PART_ADMISSION:
                rpaReason = "Defendant partial admission.";
                builder.receiptOfPartAdmission(
                    List.of(
                        Event.builder()
                            .eventSequence(4)
                            .eventCode("60")
                            .dateReceived("TODO")
                            .litigiousPartyID("002")
                            .build()
                    )
                );
                break;
            case RESPONDENT_COUNTER_CLAIM:
                rpaReason = "Defendant rejects and counter claims.";
                builder.defenceAndCounterClaim(
                    List.of(
                        Event.builder()
                            .eventSequence(4)
                            .eventCode("52")
                            .dateReceived("TODO")
                            .litigiousPartyID("002")
                            .build()
                    )
                );
                break;
            default:
                throw new IllegalArgumentException("Invalid defendant response state flow: " + mainFlowState);
        }

        builder.miscellaneous(
            List.of(
                Event.builder()
                    .eventSequence(1)
                    .eventCode("999")
                    .dateReceived("TODO")
                    .eventDetails(EventDetails.builder()
                                      .miscText("Claimant has notified defendant")
                                      .build())
                    .build(),
                Event.builder()
                    .eventSequence(5)
                    .eventCode("999")
                    .dateReceived("TODO")
                    .eventDetails(EventDetails.builder()
                                      .miscText("RPA Reason: " + rpaReason)
                                      .build())
                    .build()
            )
        ).acknowledgementOfServiceReceived(
            List.of(
                Event.builder()
                    .eventSequence(2)
                    .eventCode("38")
                    .dateReceived("TODO")
                    .litigiousPartyID("002")
                    .eventDetails(EventDetails.builder()
                                      .responseIntention("contest jurisdiction")
                                      .build())
                    .build()
            )
        ).consentExtensionFilingDefence(
            List.of(
                Event.builder()
                    .eventSequence(3)
                    .eventCode("45")
                    .dateReceived("TODO")
                    .litigiousPartyID("002")
                    .eventDetails(EventDetails.builder()
                                      .agreedExtensionDate("TODO")
                                      .build())
                    .build()
            )
        );
    }
}
