package uk.gov.hmcts.reform.unspec.enums;

import uk.gov.hmcts.reform.unspec.model.ClaimValue;

import java.math.BigDecimal;

public enum AllocatedTrack {
    SMALL_CLAIM,
    FAST_CLAIM,
    MULTI_CLAIM;

    public static AllocatedTrack getAllocatedTrack(ClaimValue claimValue, ClaimType claimType) {
        if (claimType.isPersonalInjury()) {
            if (claimValue.getHigherValue().compareTo(BigDecimal.valueOf(1000)) < 0) {
                return SMALL_CLAIM;
            } else if ((claimValue.getHigherValue().compareTo(BigDecimal.valueOf(1000)) == 0
                || claimValue.getHigherValue().compareTo(BigDecimal.valueOf(1000)) > 0)
                && (claimValue.getHigherValue().compareTo(BigDecimal.valueOf(25000)) < 0
                || claimValue.getHigherValue().compareTo(BigDecimal.valueOf(25000)) == 0)) {
                return FAST_CLAIM;
            }
        }

        if (claimValue.getHigherValue().compareTo(BigDecimal.valueOf(10000)) < 0) {
            return SMALL_CLAIM;
        } else if ((claimValue.getHigherValue().compareTo(BigDecimal.valueOf(10000)) == 0
            || claimValue.getHigherValue().compareTo(BigDecimal.valueOf(10000)) > 0)
            && (claimValue.getHigherValue().compareTo(BigDecimal.valueOf(25000)) < 0
            || claimValue.getHigherValue().compareTo(BigDecimal.valueOf(25000)) == 0)) {
            return FAST_CLAIM;
        } else {
            return MULTI_CLAIM;
        }
    }
}
