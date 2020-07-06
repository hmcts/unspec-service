package uk.gov.hmcts.reform.ucmc.callback;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CaseEvent {
    CREATE_CASE("CreateClaim"),
    ISSUE_CASE("IssueClaim");

    private final String value;
}
