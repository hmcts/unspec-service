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
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.CallbackRequest;
import uk.gov.hmcts.reform.fees.client.FeesClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Api
@Slf4j
@RestController
@RequestMapping("/callback/fees")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FeesController {

    private final FeesClient feesClient;

    @PostMapping("/mid-event")
    public AboutToStartOrSubmitCallbackResponse handleMidEvent(@RequestBody CallbackRequest callbackRequest) {
        var caseData = callbackRequest.getCaseDetails().getData();

        var claimValuePence = new BigDecimal(caseData.get("claimValue").toString());
        var claimValuePounds = claimValuePence.divide(BigDecimal.valueOf(100), RoundingMode.UP);

        List<String> errors = new ArrayList<>();
        try {
            var feeAmountPounds = feesClient.lookupFee("default", "issue", claimValuePounds).getFeeAmount();
            var feeAmountPence = feeAmountPounds.multiply(BigDecimal.valueOf(100)).toBigInteger();
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
        var caseData = callbackRequest.getCaseDetails().getData();

        //TODO: make test payment here
        caseData.remove("feeAmount");

        return AboutToStartOrSubmitCallbackResponse.builder()
            .data(caseData)
            .build();
    }
}
