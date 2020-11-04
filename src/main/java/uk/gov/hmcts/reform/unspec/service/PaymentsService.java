package uk.gov.hmcts.reform.unspec.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.payments.client.PaymentsClient;
import uk.gov.hmcts.reform.payments.client.models.FeeDto;
import uk.gov.hmcts.reform.payments.client.models.PaymentDto;
import uk.gov.hmcts.reform.payments.client.request.CreditAccountPaymentRequest;
import uk.gov.hmcts.reform.unspec.config.PaymentsConfiguration;
import uk.gov.hmcts.reform.unspec.model.CaseData;
import uk.gov.hmcts.reform.unspec.model.ClaimFee;
import uk.gov.hmcts.reform.unspec.request.RequestData;
import uk.gov.hmcts.reform.unspec.utils.MonetaryConversions;

import java.math.BigDecimal;
import javax.validation.constraints.NotNull;

import static uk.gov.hmcts.reform.unspec.utils.MonetaryConversions.penniesToPounds;

@Service
@RequiredArgsConstructor
public class PaymentsService {

    private final PaymentsClient paymentsClient;
    private final RequestData requestData;
    private final PaymentsConfiguration paymentsConfiguration;

    public PaymentDto createCreditAccountPayment(CaseData caseData) {
        return paymentsClient.createCreditAccountPayment(
            requestData.authorisation(),
            buildRequest(caseData)
        );
    }

    private CreditAccountPaymentRequest buildRequest(CaseData caseData) {
        String ccdCaseId = String.valueOf(caseData.getCcdCaseReference());
        ClaimFee claimFee = caseData.getClaimFee();
        FeeDto[] fees = buildFees(ccdCaseId, claimFee);
        return CreditAccountPaymentRequest.builder()
            .accountNumber(caseData.getPbaNumber().name())
            .amount(penniesToPounds(claimFee.getFeeAmount()))
            .caseReference(caseData.getLegacyCaseReference())
            .ccdCaseNumber(ccdCaseId)
            .customerReference("Test Customer Reference")
            .description("Claim issue payment")
            .organisationName("Test Organisation Name")
            .service(paymentsConfiguration.getService())
            .siteId(paymentsConfiguration.getSiteId())
            .fees(fees)
            .build();
    }

    private FeeDto[] buildFees(@NotNull String ccdCaseId, @NotNull ClaimFee claimFee) {
        return new FeeDto[]{
            FeeDto.builder()
                .ccdCaseNumber(ccdCaseId)
                .calculatedAmount(penniesToPounds(claimFee.getFeeAmount()))
                .code(claimFee.getCode())
                .version(String.valueOf(claimFee.getVersion()))
                .build()
        };
    }
}
