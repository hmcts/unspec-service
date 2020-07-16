package uk.gov.hmcts.reform.ucmc.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClaimValue {
    private final String lowerValue;
    private final String higherValue;

    public boolean hasLargerLowerValue() {
        if (lowerValue == null || higherValue == null) {
            return false;
        }

        return new BigDecimal(lowerValue).compareTo(new BigDecimal(higherValue)) > 0;
    }

    public String formData() {
        if (lowerValue == null) {
            return "up to " + higherValue;
        }
        return lowerValue + " - " + higherValue;
    }
}
