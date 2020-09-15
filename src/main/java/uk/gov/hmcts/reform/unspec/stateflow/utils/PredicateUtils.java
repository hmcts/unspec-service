package uk.gov.hmcts.reform.unspec.stateflow.utils;

import uk.gov.hmcts.reform.unspec.model.CaseData;

import java.util.function.Predicate;

import static uk.gov.hmcts.reform.unspec.enums.CaseState.STAYED;

public class PredicateUtils {

    private PredicateUtils() {
        //Utility class
    }

    public static Predicate<CaseData> claimantIssueClaim = caseData ->
        caseData.getClaimIssuedDate() != null
            && caseData.getLegacyCaseReference() != null;

    public static Predicate<CaseData> claimantConfirmService = caseData ->
        caseData.getDeemedServiceDateToRespondentSolicitor1() != null
            && caseData.getRespondentSolicitor1ResponseDeadline() != null;

    public static Predicate<CaseData> defendantAcknowledgeService = caseData ->
        caseData.getRespondent1ClaimResponseIntentionType() != null;

    public static Predicate<CaseData> defendantRespondToClaim = caseData ->
        caseData.getApplicantSolicitorResponseDeadlineToRespondentSolicitor1() != null;

    public static Predicate<CaseData> defendantAskForAnExtension = caseData ->
        caseData.getRespondentSolicitor1claimResponseExtensionProposedDeadline() != null;

    public static Predicate<CaseData> claimantRespondToRequestForExtension = caseData ->
        caseData.getRespondentSolicitor1claimResponseExtensionAccepted() != null;

    public static Predicate<CaseData> claimantRespondToDefence = caseData ->
        caseData.getApplicant1ProceedWithClaim() != null
            && caseData.getApplicant1DefenceResponseDocument() != null;

    public static Predicate<CaseData> schedulerStayClaim = caseData ->
        caseData.getCcdState() == STAYED;

}
