package uk.gov.hmcts.reform.unspec.enums;

import lombok.Getter;

@Getter
public enum CaseRole {
    APPLICANTSOLICITOR1,
    APPLICANTSOLICITOR2,
    RESPONDENTSOLICITOR1,
    RESPONDENTSOLICITOR2;

    private String formattedName;

    CaseRole() {
        this.formattedName = String.format("[%s]", name());
    }
}
