package uk.gov.hmcts.reform.unspec.service.flowstate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.unspec.helpers.CaseDetailsConverter;
import uk.gov.hmcts.reform.unspec.model.CaseData;
import uk.gov.hmcts.reform.unspec.stateflow.StateFlow;
import uk.gov.hmcts.reform.unspec.stateflow.StateFlowBuilder;
import uk.gov.hmcts.reform.unspec.stateflow.model.State;

import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.applicantRespondToDefence;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.caseProceedsInCaseman;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.claimDetailsNotified;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.claimDiscontinued;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.claimIssued;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.claimNotified;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.claimTakenOffline;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.claimWithdrawn;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.paymentFailed;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.paymentSuccessful;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.pendingCaseIssued;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.respondent1NotRepresented;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.respondentAcknowledgeService;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.respondentAgreedExtension;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.respondentCounterClaim;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.respondentFullAdmission;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.respondentFullDefence;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.respondentPartAdmission;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.schedulerStayClaim;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.APPLICANT_RESPOND_TO_DEFENCE;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.AWAITING_CASE_DETAILS_NOTIFICATION;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.AWAITING_CASE_NOTIFICATION;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.CASE_PROCEEDS_IN_CASEMAN;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.CLAIM_DISCONTINUED;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.CLAIM_ISSUED;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.CLAIM_STAYED;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.CLAIM_WITHDRAWN;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.DRAFT;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.EXTENSION_REQUESTED;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.FLOW_NAME;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.PAYMENT_FAILED;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.PAYMENT_SUCCESSFUL;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.PENDING_CASE_ISSUED;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.PROCEEDS_OFFLINE_ADMIT_OR_COUNTER_CLAIM;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.PROCEEDS_OFFLINE_UNREPRESENTED_DEFENDANT;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.RESPONDENT_COUNTER_CLAIM;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.RESPONDENT_FULL_ADMISSION;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.RESPONDENT_FULL_DEFENCE;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.RESPONDENT_PART_ADMISSION;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.SERVICE_ACKNOWLEDGED;

@Component
@RequiredArgsConstructor
public class StateFlowEngine {

    private final CaseDetailsConverter caseDetailsConverter;

    public StateFlow build() {
        return StateFlowBuilder.<FlowState.Main>flow(FLOW_NAME)
            .initial(DRAFT)
                .transitionTo(PENDING_CASE_ISSUED).onlyIf(pendingCaseIssued)
            .state(PENDING_CASE_ISSUED)
                .transitionTo(PAYMENT_SUCCESSFUL).onlyIf(paymentSuccessful)
                .transitionTo(PAYMENT_FAILED).onlyIf(paymentFailed)
            .state(PAYMENT_FAILED)
                .transitionTo(PAYMENT_SUCCESSFUL).onlyIf(paymentSuccessful)
            .state(PAYMENT_SUCCESSFUL)
                .transitionTo(AWAITING_CASE_NOTIFICATION).onlyIf(claimIssued)
                .transitionTo(PROCEEDS_OFFLINE_UNREPRESENTED_DEFENDANT).onlyIf(respondent1NotRepresented)
            .state(AWAITING_CASE_NOTIFICATION)
                .transitionTo(AWAITING_CASE_DETAILS_NOTIFICATION).onlyIf(claimNotified)
                .transitionTo(CASE_PROCEEDS_IN_CASEMAN).onlyIf(caseProceedsInCaseman)
            .state(AWAITING_CASE_DETAILS_NOTIFICATION)
                .transitionTo(CLAIM_ISSUED).onlyIf(claimDetailsNotified)
                .transitionTo(CASE_PROCEEDS_IN_CASEMAN).onlyIf(caseProceedsInCaseman)
            .state(CLAIM_ISSUED)
                .transitionTo(SERVICE_ACKNOWLEDGED).onlyIf(respondentAcknowledgeService)
                .transitionTo(RESPONDENT_FULL_DEFENCE).onlyIf(respondentFullDefence)
                .transitionTo(RESPONDENT_FULL_ADMISSION).onlyIf(respondentFullAdmission)
                .transitionTo(RESPONDENT_PART_ADMISSION).onlyIf(respondentPartAdmission)
                .transitionTo(RESPONDENT_COUNTER_CLAIM).onlyIf(respondentCounterClaim)
                .transitionTo(CLAIM_WITHDRAWN).onlyIf(claimWithdrawn)
                .transitionTo(CLAIM_DISCONTINUED).onlyIf(claimDiscontinued)
                .transitionTo(CLAIM_STAYED).onlyIf(schedulerStayClaim)
                .transitionTo(CASE_PROCEEDS_IN_CASEMAN).onlyIf(caseProceedsInCaseman)
            .state(SERVICE_ACKNOWLEDGED)
                .transitionTo(EXTENSION_REQUESTED).onlyIf(respondentAgreedExtension)
                .transitionTo(CLAIM_DISCONTINUED).onlyIf(claimDiscontinued)
                .transitionTo(CLAIM_WITHDRAWN).onlyIf(claimWithdrawn)
                .transitionTo(CASE_PROCEEDS_IN_CASEMAN).onlyIf(caseProceedsInCaseman)
            .state(EXTENSION_REQUESTED)
                .transitionTo(RESPONDENT_FULL_DEFENCE).onlyIf(respondentFullDefence)
                .transitionTo(RESPONDENT_FULL_ADMISSION).onlyIf(respondentFullAdmission)
                .transitionTo(RESPONDENT_PART_ADMISSION).onlyIf(respondentPartAdmission)
                .transitionTo(RESPONDENT_COUNTER_CLAIM).onlyIf(respondentCounterClaim)
                .transitionTo(CLAIM_DISCONTINUED).onlyIf(claimDiscontinued)
                .transitionTo(CLAIM_WITHDRAWN).onlyIf(claimWithdrawn)
                .transitionTo(CASE_PROCEEDS_IN_CASEMAN).onlyIf(caseProceedsInCaseman)
            .state(RESPONDENT_FULL_DEFENCE)
                .transitionTo(APPLICANT_RESPOND_TO_DEFENCE).onlyIf(applicantRespondToDefence)
                .transitionTo(CLAIM_DISCONTINUED).onlyIf(claimDiscontinued)
                .transitionTo(CLAIM_WITHDRAWN).onlyIf(claimWithdrawn)
                .transitionTo(CASE_PROCEEDS_IN_CASEMAN).onlyIf(caseProceedsInCaseman)
                .transitionTo(PROCEEDS_OFFLINE_ADMIT_OR_COUNTER_CLAIM).onlyIf(claimTakenOffline)
            .state(RESPONDENT_FULL_ADMISSION)
                .transitionTo(CLAIM_DISCONTINUED).onlyIf(claimDiscontinued)
                .transitionTo(CLAIM_WITHDRAWN).onlyIf(claimWithdrawn)
                .transitionTo(CASE_PROCEEDS_IN_CASEMAN).onlyIf(caseProceedsInCaseman)
                .transitionTo(PROCEEDS_OFFLINE_ADMIT_OR_COUNTER_CLAIM).onlyIf(claimTakenOffline)
            .state(RESPONDENT_PART_ADMISSION)
                .transitionTo(CLAIM_DISCONTINUED).onlyIf(claimDiscontinued)
                .transitionTo(CLAIM_WITHDRAWN).onlyIf(claimWithdrawn)
                .transitionTo(CASE_PROCEEDS_IN_CASEMAN).onlyIf(caseProceedsInCaseman)
                .transitionTo(PROCEEDS_OFFLINE_ADMIT_OR_COUNTER_CLAIM).onlyIf(claimTakenOffline)
            .state(RESPONDENT_COUNTER_CLAIM)
                .transitionTo(CLAIM_DISCONTINUED).onlyIf(claimDiscontinued)
                .transitionTo(CLAIM_WITHDRAWN).onlyIf(claimWithdrawn)
                .transitionTo(CASE_PROCEEDS_IN_CASEMAN).onlyIf(caseProceedsInCaseman)
                .transitionTo(PROCEEDS_OFFLINE_ADMIT_OR_COUNTER_CLAIM).onlyIf(claimTakenOffline)
            .state(APPLICANT_RESPOND_TO_DEFENCE)
                .transitionTo(CLAIM_STAYED)
            .state(CLAIM_STAYED)
            .state(CASE_PROCEEDS_IN_CASEMAN)
            .state(PROCEEDS_OFFLINE_UNREPRESENTED_DEFENDANT)
            .state(PROCEEDS_OFFLINE_ADMIT_OR_COUNTER_CLAIM)
            .state(CLAIM_WITHDRAWN)
            .state(CLAIM_DISCONTINUED)
            .build();
    }

    public StateFlow evaluate(CaseDetails caseDetails) {
        return evaluate(caseDetailsConverter.toCaseData(caseDetails));
    }

    public StateFlow evaluate(CaseData caseData) {
        return build().evaluate(caseData);
    }

    public boolean hasTransitionedTo(CaseDetails caseDetails, FlowState.Main state) {
        return evaluate(caseDetails).getStateHistory().stream()
            .map(State::getName)
            .anyMatch(name -> name.equals(state.fullName()));
    }
}
