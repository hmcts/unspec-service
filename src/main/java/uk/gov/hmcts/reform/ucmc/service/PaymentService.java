package uk.gov.hmcts.reform.ucmc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.payments.client.PaymentsClient;
import uk.gov.hmcts.reform.payments.client.models.FeeDto;
import uk.gov.hmcts.reform.payments.client.models.PaymentDto;
import uk.gov.hmcts.reform.payments.client.request.CreditAccountPaymentRequest;
import uk.gov.hmcts.reform.ucmc.request.RequestData;

import java.math.BigDecimal;

@Service
public class PaymentService {

    private final FeeService feeService;
    private final PaymentsClient paymentsClient;
    private final RequestData requestData;
    private final String siteId;
    private final String service;

    @Autowired
    public PaymentService(FeeService feeService,
                          PaymentsClient paymentsClient,
                          RequestData requestData,
                          @Value("${payments.api.site_id:}") String siteId,
                          @Value("${payments.api.service:}") String service) {
        this.feeService = feeService;
        this.paymentsClient = paymentsClient;
        this.requestData = requestData;
        this.siteId = siteId;
        this.service = service;
    }

    public PaymentDto createCreditAccountPayment(CaseDetails caseDetails) {
        var claimValue = new BigDecimal(caseDetails.getData().get("claimValue").toString());
        //TODO: consider if fees register should be called again or fees data should be stored in the case
        FeeDto feeDto = feeService.getFeeDataByClaimValue(claimValue);

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
            .caseReference(String.valueOf(caseDetails.getId()))
            .ccdCaseNumber(String.valueOf(caseDetails.getId()))
            .customerReference(caseData.get("customerReference").toString())
            .description(caseData.get("description").toString())
            .organisationName(caseData.get("organisationName").toString())
            .service(service)
            .siteId(siteId)
            .fees(new FeeDto[]{feeDto})
            .build();
    }
}
