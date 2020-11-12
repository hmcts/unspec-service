package uk.gov.hmcts.reform.unspec.model;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.reform.unspec.enums.PaymentStatus;

@Data
@Builder
public class PaymentDetails {

    private PaymentStatus status;
    private String reference;
    private String errorMessage;
    private String errorCode;
}
