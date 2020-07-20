package uk.gov.hmcts.reform.unspec.model;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import uk.gov.hmcts.reform.unspec.utils.MonetaryConversions;

import java.math.BigDecimal;

@Data
@Builder
@RequiredArgsConstructor
public class ClaimValue {
    private final Integer lowerValue;
    private final Integer higherValue;

    public boolean hasLargerLowerValue() {
        if (lowerValue == null || higherValue == null) {
            return false;
        }

        return lowerValue > higherValue;
    }

    public String formData() {
        BigDecimal higherAmount = MonetaryConversions.penniesToPounds(new BigDecimal(higherValue));
        if (lowerValue == null) {
            return "up to £" + higherAmount;
        }
        return "£" + MonetaryConversions.penniesToPounds(new BigDecimal(lowerValue)) + " - £" + higherAmount;
    }
}
