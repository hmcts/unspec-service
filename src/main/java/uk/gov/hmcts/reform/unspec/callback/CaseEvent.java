package uk.gov.hmcts.reform.unspec.callback;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CaseEvent {
    CREATE_CLAIM("Create claim", UserType.USER),
    CONFIRM_SERVICE("Confirm service", UserType.USER),
    REQUEST_EXTENSION("Request extension", UserType.USER),
    RESPOND_EXTENSION("Respond to extension request", UserType.USER),
    MOVE_TO_STAYED("Move case to stayed", UserType.USER),
    ACKNOWLEDGE_SERVICE("Acknowledge service", UserType.USER),
    DEFENDANT_RESPONSE("Respond to claim", UserType.USER),
    CLAIMANT_RESPONSE("View and respond to defence", UserType.USER),
    WITHDRAW_CLAIM("Withdraw claim", UserType.USER),
    DISCONTINUE_CLAIM("Discontinue claim", UserType.USER),
    NOTIFY_RESPONDENT_SOLICITOR1_FOR_CLAIM_ISSUE("Notify claim issue", UserType.CAMUNDA),
    NOTIFY_APPLICANT_SOLICITOR1_FOR_REQUEST_FOR_EXTENSION("Notify request for extension", UserType.CAMUNDA),
    NOTIFY_RESPONDENT_SOLICITOR1_FOR_EXTENSION_RESPONSE("Notify extension response", UserType.CAMUNDA),
    NOTIFY_APPLICANT_SOLICITOR1_FOR_SERVICE_ACKNOWLEDGEMENT("Notify service acknowledgement", UserType.CAMUNDA),
    NOTIFY_APPLICANT_SOLICITOR1_FOR_DEFENDANT_RESPONSE("Notify response", UserType.CAMUNDA),
    NOTIFY_RESPONDENT_SOLICITOR1_FOR_CASE_HANDED_OFFLINE("Notify handed offline", UserType.CAMUNDA),
    NOTIFY_APPLICANT_SOLICITOR1_FOR_CASE_HANDED_OFFLINE("Notify handed offline", UserType.CAMUNDA),
    NOTIFY_RESPONDENT_SOLICITOR1_FOR_CASE_TRANSFERRED_TO_LOCAL_COURT(
        "Notify transferred local court",
        UserType.CAMUNDA
    ),
    NOTIFY_APPLICANT_SOLICITOR1_FOR_CASE_TRANSFERRED_TO_LOCAL_COURT(
        "Notify transferred local court",
        UserType.CAMUNDA
    ),
    START_BUSINESS_PROCESS("Start business process", UserType.CAMUNDA);

    private final String displayName;
    private final UserType userType;
}
