package uk.gov.hmcts.reform.unspec.service.flowstate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.unspec.callback.CaseEvent;
import uk.gov.hmcts.reform.unspec.model.CaseData;
import uk.gov.hmcts.reform.unspec.stateflow.StateFlow;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Map.entry;
import static uk.gov.hmcts.reform.unspec.callback.CaseEvent.ACKNOWLEDGE_SERVICE;
import static uk.gov.hmcts.reform.unspec.callback.CaseEvent.ADD_DEFENDANT_LITIGATION_FRIEND;
import static uk.gov.hmcts.reform.unspec.callback.CaseEvent.ADD_OR_AMEND_CLAIM_DOCUMENTS;
import static uk.gov.hmcts.reform.unspec.callback.CaseEvent.AMEND_PARTY_DETAILS;
import static uk.gov.hmcts.reform.unspec.callback.CaseEvent.CASE_PROCEEDS_IN_CASEMAN;
import static uk.gov.hmcts.reform.unspec.callback.CaseEvent.CLAIMANT_RESPONSE;
import static uk.gov.hmcts.reform.unspec.callback.CaseEvent.CREATE_CLAIM;
import static uk.gov.hmcts.reform.unspec.callback.CaseEvent.DEFENDANT_RESPONSE;
import static uk.gov.hmcts.reform.unspec.callback.CaseEvent.DISCONTINUE_CLAIM;
import static uk.gov.hmcts.reform.unspec.callback.CaseEvent.INFORM_AGREED_EXTENSION_DATE;
import static uk.gov.hmcts.reform.unspec.callback.CaseEvent.MOVE_CLAIM_TO_STRUCK_OUT;
import static uk.gov.hmcts.reform.unspec.callback.CaseEvent.MOVE_TO_STAYED;
import static uk.gov.hmcts.reform.unspec.callback.CaseEvent.NOTIFY_DEFENDANT_OF_CLAIM;
import static uk.gov.hmcts.reform.unspec.callback.CaseEvent.NOTIFY_DEFENDANT_OF_CLAIM_DETAILS;
import static uk.gov.hmcts.reform.unspec.callback.CaseEvent.RESUBMIT_CLAIM;
import static uk.gov.hmcts.reform.unspec.callback.CaseEvent.WITHDRAW_CLAIM;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.APPLICANT_RESPOND_TO_DEFENCE;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.AWAITING_CASE_DETAILS_NOTIFICATION;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.AWAITING_CASE_NOTIFICATION;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.CLAIM_ISSUED;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.CLAIM_STAYED;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.DRAFT;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.EXTENSION_REQUESTED;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.PAYMENT_FAILED;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.RESPONDENT_COUNTER_CLAIM;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.RESPONDENT_FULL_ADMISSION;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.RESPONDENT_FULL_DEFENCE;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.RESPONDENT_PART_ADMISSION;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.SERVICE_ACKNOWLEDGED;

@Service
@RequiredArgsConstructor
public class FlowStateAllowedEventService {

    private final StateFlowEngine stateFlowEngine;

    private static final Map<String, List<CaseEvent>> ALLOWED_EVENTS_ON_FLOW_STATE = Map.ofEntries(
        entry(
            DRAFT.fullName(),
            List.of(
                CREATE_CLAIM,
                WITHDRAW_CLAIM,
                DISCONTINUE_CLAIM
            )
        ),

        entry(
            PAYMENT_FAILED.fullName(),
            List.of(
                RESUBMIT_CLAIM,
                WITHDRAW_CLAIM,
                DISCONTINUE_CLAIM,
                AMEND_PARTY_DETAILS
            )
        ),

        entry(
            AWAITING_CASE_NOTIFICATION.fullName(),
            List.of(
                NOTIFY_DEFENDANT_OF_CLAIM,
                ADD_DEFENDANT_LITIGATION_FRIEND,
                CASE_PROCEEDS_IN_CASEMAN,
                ADD_OR_AMEND_CLAIM_DOCUMENTS,
                AMEND_PARTY_DETAILS
            )
        ),

        entry(
            AWAITING_CASE_DETAILS_NOTIFICATION.fullName(),
            List.of(
                NOTIFY_DEFENDANT_OF_CLAIM_DETAILS,
                ADD_DEFENDANT_LITIGATION_FRIEND,
                CASE_PROCEEDS_IN_CASEMAN,
                ADD_OR_AMEND_CLAIM_DOCUMENTS,
                AMEND_PARTY_DETAILS
            )
        ),

        entry(
            CLAIM_ISSUED.fullName(),
            List.of(
                MOVE_TO_STAYED,
                ACKNOWLEDGE_SERVICE,
                ADD_DEFENDANT_LITIGATION_FRIEND,
                WITHDRAW_CLAIM,
                DISCONTINUE_CLAIM,
                CASE_PROCEEDS_IN_CASEMAN,
                AMEND_PARTY_DETAILS
            )
        ),

        entry(
            EXTENSION_REQUESTED.fullName(),
            List.of(
                DEFENDANT_RESPONSE,
                ADD_DEFENDANT_LITIGATION_FRIEND,
                WITHDRAW_CLAIM,
                DISCONTINUE_CLAIM,
                CASE_PROCEEDS_IN_CASEMAN,
                AMEND_PARTY_DETAILS
            )
        ),

        entry(
            CLAIM_STAYED.fullName(),
            List.of(
                WITHDRAW_CLAIM,
                DISCONTINUE_CLAIM
            )
        ),

        entry(
            SERVICE_ACKNOWLEDGED.fullName(),
            List.of(
                DEFENDANT_RESPONSE,
                ADD_DEFENDANT_LITIGATION_FRIEND,
                WITHDRAW_CLAIM,
                DISCONTINUE_CLAIM,
                CASE_PROCEEDS_IN_CASEMAN,
                INFORM_AGREED_EXTENSION_DATE,
                AMEND_PARTY_DETAILS
            )
        ),

        entry(
            RESPONDENT_FULL_DEFENCE.fullName(),
            List.of(
                CLAIMANT_RESPONSE,
                WITHDRAW_CLAIM,
                ADD_DEFENDANT_LITIGATION_FRIEND,
                DISCONTINUE_CLAIM,
                MOVE_CLAIM_TO_STRUCK_OUT,
                CASE_PROCEEDS_IN_CASEMAN,
                AMEND_PARTY_DETAILS
            )
        ),

        entry(
            RESPONDENT_FULL_ADMISSION.fullName(),
            List.of(
                WITHDRAW_CLAIM,
                ADD_DEFENDANT_LITIGATION_FRIEND,
                DISCONTINUE_CLAIM,
                CASE_PROCEEDS_IN_CASEMAN,
                AMEND_PARTY_DETAILS
            )
        ),

        entry(
            RESPONDENT_PART_ADMISSION.fullName(),
            List.of(
                WITHDRAW_CLAIM,
                ADD_DEFENDANT_LITIGATION_FRIEND,
                DISCONTINUE_CLAIM,
                CASE_PROCEEDS_IN_CASEMAN,
                AMEND_PARTY_DETAILS
            )
        ),

        entry(
            RESPONDENT_COUNTER_CLAIM.fullName(),
            List.of(
                WITHDRAW_CLAIM,
                ADD_DEFENDANT_LITIGATION_FRIEND,
                DISCONTINUE_CLAIM,
                CASE_PROCEEDS_IN_CASEMAN,
                AMEND_PARTY_DETAILS
            )
        ),

        entry(
            APPLICANT_RESPOND_TO_DEFENCE.fullName(),
            List.of(
                ADD_DEFENDANT_LITIGATION_FRIEND,
                WITHDRAW_CLAIM,
                DISCONTINUE_CLAIM,
                CASE_PROCEEDS_IN_CASEMAN,
                AMEND_PARTY_DETAILS
            )
        )
    );

    public FlowState getFlowState(CaseData caseData) {
        StateFlow stateFlow = stateFlowEngine.evaluate(caseData);
        return FlowState.fromFullName(stateFlow.getState().getName());
    }

    public List<CaseEvent> getAllowedEvents(String stateFullName) {
        return ALLOWED_EVENTS_ON_FLOW_STATE.getOrDefault(stateFullName, emptyList());
    }

    public boolean isAllowedOnState(String stateFullName, CaseEvent caseEvent) {
        return ALLOWED_EVENTS_ON_FLOW_STATE
            .getOrDefault(stateFullName, emptyList())
            .contains(caseEvent);
    }

    public boolean isAllowed(CaseDetails caseDetails, CaseEvent caseEvent) {
        StateFlow stateFlow = stateFlowEngine.evaluate(caseDetails);
        return isAllowedOnState(stateFlow.getState().getName(), caseEvent);
    }

    public List<String> getAllowedStates(CaseEvent caseEvent) {
        return ALLOWED_EVENTS_ON_FLOW_STATE.entrySet().stream()
            .filter(entry -> entry.getValue().contains(caseEvent))
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }
}
