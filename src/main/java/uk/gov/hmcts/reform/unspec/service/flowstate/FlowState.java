package uk.gov.hmcts.reform.unspec.service.flowstate;

import static org.springframework.util.StringUtils.isEmpty;

public interface FlowState {

    String fullName();

    static FlowState fromFullName(String fullName) {
        if (isEmpty(fullName)) {
            throw new IllegalArgumentException("Invalid full name:" + fullName);
        }
        int lastIndexOfDot = fullName.lastIndexOf('.');
        String flowStateName = fullName.substring(lastIndexOfDot + 1);
        String flowName = fullName.substring(0, lastIndexOfDot);
        switch (flowName) {
            case "MAIN":
                return MainFlowState.valueOf(flowStateName);
            default:
                throw new IllegalArgumentException("Invalid flow name:" + flowName);
        }
    }
}
