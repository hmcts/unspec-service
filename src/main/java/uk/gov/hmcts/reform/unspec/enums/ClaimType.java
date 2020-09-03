package uk.gov.hmcts.reform.unspec.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ClaimType {
    PERSONAL_INJURY(SubType.PERSONAL_INJURY),
    CLINICAL_NEGLIGENCE(SubType.PERSONAL_INJURY),
    BREACH_OF_CONTRACT(SubType.OTHER),
    CONSUMER_CREDIT(SubType.OTHER),
    OTHER(SubType.OTHER);

    private final SubType subType;

    public enum SubType {
        PERSONAL_INJURY,
        OTHER
    }

    public boolean isPersonalInjury() {
        return this.subType.equals(SubType.PERSONAL_INJURY);
    }
}
