package uk.gov.hmcts.reform.ucmc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.payments.client.models.PaymentDto;
import uk.gov.hmcts.reform.ucmc.fnp.client.PaymentApi;
import uk.gov.hmcts.reform.ucmc.fnp.model.payment.CreditAccountPaymentRequest;
import uk.gov.hmcts.reform.ucmc.fnp.model.payment.FeeDto;
import uk.gov.hmcts.reform.ucmc.request.RequestData;

import java.math.BigDecimal;
import java.util.List;

import static uk.gov.hmcts.reform.ucmc.fnp.model.payment.enums.Currency.GBP;
import static uk.gov.hmcts.reform.ucmc.fnp.model.payment.enums.Service.CMC;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PaymentService {

    private static final String SITE_ID = "AA00";

    private final FeeService feeService;
    private final PaymentApi paymentApi;
    private final RequestData requestData;
    private final AuthTokenGenerator authTokenGenerator;

    public PaymentDto createCreditAccountPayment(CaseDetails caseDetails) {
        var claimValue = new BigDecimal(caseDetails.getData().get("claimValue").toString());
        FeeDto feeDto = feeService.getFeeDataByClaimValue(claimValue);

        return paymentApi.createCreditAccountPayment(
            requestData.authorisation(),
            authTokenGenerator.generate(),
            buildRequest(caseDetails, feeDto)
        );
    }

    private CreditAccountPaymentRequest buildRequest(CaseDetails caseDetails, FeeDto feeDto) {
        var caseData = caseDetails.getData();

        return CreditAccountPaymentRequest.builder()
            .accountNumber(caseData.get("pbaNumber").toString())
            .amount(feeDto.getCalculatedAmount())
            .caseReference(String.valueOf(caseDetails.getId()))
            .ccdCaseNumber(String.valueOf(caseDetails.getId()))
            .currency(GBP)
            .customerReference(caseData.get("customerReference").toString())
            .description(caseData.get("description").toString())
            .organisationName(caseData.get("organisationName").toString())
            .service(CMC)
            .siteId(SITE_ID)
            .fees(List.of(feeDto))
            .build();
    }
}
