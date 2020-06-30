package uk.gov.hmcts.reform.ucmc.fnp.model.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class FeeDto {
    @JsonProperty("calculated_amount")
    private BigDecimal calculatedAmount;
    private Integer version;
    private String code;
}
