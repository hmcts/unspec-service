package uk.gov.hmcts.reform.unspec.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.reform.unspec.utils.MonetaryConversions;

import java.math.BigDecimal;

@Data
@Builder
public class ClaimValue {

    private final BigDecimal statementOfValue;

    @JsonCreator
    public ClaimValue(@JsonProperty("statementOfValue") BigDecimal statementOfValue) {
        this.statementOfValue = statementOfValue;
    }

    public String formData() {
        BigDecimal statementOfValue = MonetaryConversions.penniesToPounds(this.statementOfValue);

        return "up to £" + statementOfValue;
    }
}
