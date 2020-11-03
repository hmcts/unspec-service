package uk.gov.hmcts.reform.unspec.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.payments.client.PaymentsClient;
import uk.gov.hmcts.reform.payments.client.models.FeeDto;
import uk.gov.hmcts.reform.payments.client.models.PaymentDto;
import uk.gov.hmcts.reform.payments.client.request.CreditAccountPaymentRequest;
import uk.gov.hmcts.reform.unspec.config.PaymentsConfiguration;
import uk.gov.hmcts.reform.unspec.model.CaseData;
import uk.gov.hmcts.reform.unspec.request.RequestData;

@Service
@RequiredArgsConstructor
public class PaymentsService {

    private final PaymentsClient paymentsClient;
    private final RequestData requestData;
    private final PaymentsConfiguration paymentsConfiguration;

    public PaymentDto createCreditAccountPayment(CaseData caseData, FeeDto feeDto) {
        return paymentsClient.createCreditAccountPayment(
            requestData.authorisation(),
            buildRequest(caseData, feeDto)
        );
    }

    private CreditAccountPaymentRequest buildRequest(CaseData caseData, FeeDto feeDto) {
        return CreditAccountPaymentRequest.builder()
            .accountNumber(caseData.getPbaNumber().name())
            .amount(feeDto.getCalculatedAmount())
            .caseReference(caseData.getLegacyCaseReference())
            .ccdCaseNumber(caseData.getCcdCaseReference().toString())
            .customerReference("Test Customer Reference")
            .description("Claim issue payment")
            .organisationName("Test Organisation Name")
            .service(paymentsConfiguration.getService())
            .siteId(paymentsConfiguration.getSiteId())
            .fees(new FeeDto[]{feeDto})
            .build();
    }
}
