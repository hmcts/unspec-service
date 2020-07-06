package uk.gov.hmcts.reform.ucmc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.payments.client.PaymentsClient;
import uk.gov.hmcts.reform.payments.client.models.FeeDto;
import uk.gov.hmcts.reform.payments.client.models.PaymentDto;
import uk.gov.hmcts.reform.payments.client.request.CreditAccountPaymentRequest;
import uk.gov.hmcts.reform.ucmc.config.PaymentsConfiguration;
import uk.gov.hmcts.reform.ucmc.request.RequestData;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PaymentsService {

    private final FeesService feesService;
    private final PaymentsClient paymentsClient;
    private final RequestData requestData;
    private final PaymentsConfiguration paymentsConfiguration;

    public PaymentDto createCreditAccountPayment(CaseDetails caseDetails) {
        var claimValue = new BigDecimal(caseDetails.getData().get("claimValue").toString());
        FeeDto feeDto = feesService.getFeeDataByClaimValue(claimValue);

        return paymentsClient.createCreditAccountPayment(
            requestData.authorisation(),
            buildRequest(caseDetails, feeDto)
        );
    }

    private CreditAccountPaymentRequest buildRequest(CaseDetails caseDetails, FeeDto feeDto) {
        var caseData = caseDetails.getData();

        return CreditAccountPaymentRequest.builder()
            .accountNumber(caseData.get("pbaNumber").toString())
            .amount(feeDto.getCalculatedAmount())
            .caseReference(caseData.get("caseReference").toString())
            .ccdCaseNumber(String.valueOf(caseDetails.getId()))
            .customerReference(caseData.get("customerReference").toString())
            .description(caseData.get("description").toString())
            .organisationName(caseData.get("organisationName").toString())
            .service(paymentsConfiguration.getService())
            .siteId(paymentsConfiguration.getSiteId())
            .fees(new FeeDto[]{feeDto})
            .build();
    }
}
