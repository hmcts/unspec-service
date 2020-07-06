package uk.gov.hmcts.reform.ucmc.callback;

import java.util.Arrays;

public enum CaseEvent {
    CREATE_CASE("CreateClaim"),
    ISSUE_CASE("IssueClaim");

    private final String value;

    CaseEvent(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static CaseEvent fromValue(String value) {
        return Arrays.stream(values())
            .filter(event -> event.value.equals(value))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown event name: " + value));
    }
}
