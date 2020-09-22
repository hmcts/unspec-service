package uk.gov.hmcts.reform.unspec.service.flowstate;

public enum MainFlowState implements FlowState {
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

    @Override
    public String fullName() {
        return FLOW_NAME + "." + name();
    }
}
