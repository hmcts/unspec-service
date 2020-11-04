package uk.gov.hmcts.reform.unspec.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;

@Data
@Builder
public class ClaimFee {

    private String code;
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigInteger feeAmount;
    private String version;
}
