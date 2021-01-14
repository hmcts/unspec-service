package uk.gov.hmcts.reform.unspec.enums;

import lombok.Getter;

@Getter
public enum CaseRole {
    CLAIMANTSOLICITOR1,
    CLAIMANTSOLICITOR2,
    DEFENDANTSOLICITOR1,
    DEFENDANTSOLICITOR2;

    private String formattedName;

    CaseRole() {
        this.formattedName = String.format("[%s]", name());
    }
}
