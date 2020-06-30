package uk.gov.hmcts.reform.ucmc.controllers;

import feign.FeignException;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.CallbackRequest;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.fees.client.FeesClient;
import uk.gov.hmcts.reform.fees.client.model.FeeLookupResponseDto;
import uk.gov.hmcts.reform.ucmc.fnp.client.PaymentApi;
import uk.gov.hmcts.reform.ucmc.fnp.model.payment.CreditAccountPaymentRequest;
import uk.gov.hmcts.reform.ucmc.fnp.model.payment.FeeDto;
import uk.gov.hmcts.reform.ucmc.request.RequestData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static uk.gov.hmcts.reform.ucmc.fnp.model.payment.enums.Currency.GBP;
import static uk.gov.hmcts.reform.ucmc.fnp.model.payment.enums.Service.CMC;

@Api
@Slf4j
@RestController
@RequestMapping("/callback/fees")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FeesController {

    private final FeesClient feesClient;
    private final PaymentApi paymentApi;
    private final RequestData requestData;
    private final AuthTokenGenerator authTokenGenerator;

    @PostMapping("/mid-event")
    public AboutToStartOrSubmitCallbackResponse handleMidEvent(@RequestBody CallbackRequest callbackRequest) {
        var caseData = callbackRequest.getCaseDetails().getData();

        List<String> errors = new ArrayList<>();
        try {
            FeeLookupResponseDto feeAmount = lookupFee(caseData);
            var feeAmountPence = feeAmount.getFeeAmount().multiply(BigDecimal.valueOf(100)).toBigInteger();
            caseData.put("feeAmount", feeAmountPence.toString());
        } catch (FeignException e) {
            //TODO: proper error scenario handling
            log.error("There was a problem with finding fee value: " + e.contentUTF8());
            errors.add("Fee amount could not be retrieved");
        }

        return AboutToStartOrSubmitCallbackResponse.builder()
            .errors(errors)
            .data(caseData)
            .build();
    }

    @PostMapping("/about-to-submit")
    public AboutToStartOrSubmitCallbackResponse handleAboutToSubmit(@RequestBody CallbackRequest callbackRequest) {
        CaseDetails caseDetails = callbackRequest.getCaseDetails();
        var caseData = caseDetails.getData();

        FeeLookupResponseDto feeAmount = lookupFee(caseData);
        FeeDto feeDto = FeeDto.builder().calculatedAmount(feeAmount.getFeeAmount()).code(feeAmount.getCode()).version(
            feeAmount.getVersion()).build();

        List<String> errors = new ArrayList<>();
        try {
            var paymentResponse = paymentApi.createCreditAccountPayment(
                requestData.authorisation(),
                authTokenGenerator.generate(),
                CreditAccountPaymentRequest.builder()
                    .accountNumber(caseData.get("pbaNumber").toString())
                    .amount(feeAmount.getFeeAmount())
                    .caseReference(String.valueOf(caseDetails.getId()))
                    .ccdCaseNumber(String.valueOf(caseDetails.getId()))
                    .currency(GBP)
                    .customerReference("customer reference")
                    .description("description")
                    .organisationName("organisation name")
                    .service(CMC)
                    .siteId("Y689")
                    .fees(List.of(feeDto))
                    .build()
            );
            log.info("Payment made successfully: " + paymentResponse);
            caseData.remove("feeAmount");
        } catch (FeignException e) {
            //TODO: proper error scenario handling
            log.error("There was a problem with making payment: " + e.contentUTF8());
            errors.add("Fee amount could not be retrieved");
        }

        return AboutToStartOrSubmitCallbackResponse.builder()
            .errors(errors)
            .data(caseData)
            .build();
    }

    private FeeLookupResponseDto lookupFee(Map<String, Object> caseData) {
        var claimValuePence = new BigDecimal(caseData.get("claimValue").toString());
        var claimValuePounds = claimValuePence.divide(BigDecimal.valueOf(100), RoundingMode.UP);
        return feesClient.lookupFee("default", "issue", claimValuePounds);
    }
}
