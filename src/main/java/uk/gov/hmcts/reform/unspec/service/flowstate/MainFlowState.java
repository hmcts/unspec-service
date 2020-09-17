package uk.gov.hmcts.reform.unspec.service.flowstate;

public enum MainFlowState {
    DRAFT,
    CLAIM_ISSUED,
    CLAIM_STAYED,
    SERVICE_CONFIRMED,
    SERVICE_ACKNOWLEDGED,
    EXTENSION_REQUESTED,
    EXTENSION_RESPONDED,
    RESPONDED_TO_CLAIM,
    FULL_DEFENCE;

    public static final String FLOW_NAME = "MAIN";

    public String fullName() {
        return FLOW_NAME + "." + name();
    }

    public static MainFlowState fromFullName(String fullName) {
        int lastIndexOfDot = fullName.lastIndexOf('.');
        return MainFlowState.valueOf(fullName.substring(lastIndexOfDot + 1));
    }
}
