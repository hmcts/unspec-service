package uk.gov.hmcts.reform.unspec.handler.callback;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.CallbackResponse;
import uk.gov.hmcts.reform.payments.client.models.FeeDto;
import uk.gov.hmcts.reform.unspec.callback.Callback;
import uk.gov.hmcts.reform.unspec.callback.CallbackHandler;
import uk.gov.hmcts.reform.unspec.callback.CallbackParams;
import uk.gov.hmcts.reform.unspec.callback.CaseEvent;
import uk.gov.hmcts.reform.unspec.helpers.CaseDetailsConverter;
import uk.gov.hmcts.reform.unspec.model.CaseData;
import uk.gov.hmcts.reform.unspec.model.ClaimFee;
import uk.gov.hmcts.reform.unspec.service.FeesService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static uk.gov.hmcts.reform.unspec.callback.CallbackType.ABOUT_TO_SUBMIT;
import static uk.gov.hmcts.reform.unspec.callback.CaseEvent.CALCULATE_CLAIM_FEE;
import static uk.gov.hmcts.reform.unspec.utils.MonetaryConversions.poundsToPennies;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalculateClaimFeeCallbackHandler extends CallbackHandler {

    private static final List<CaseEvent> EVENTS = Collections.singletonList(CALCULATE_CLAIM_FEE);

    private final FeesService feesService;
    private final CaseDetailsConverter caseDetailsConverter;

    @Override
    protected Map<String, Callback> callbacks() {
        return Map.of(callbackKey(ABOUT_TO_SUBMIT), this::makePbaPayment);
    }

    @Override
    public List<CaseEvent> handledEvents() {
        return EVENTS;
    }

    private CallbackResponse makePbaPayment(CallbackParams callbackParams) {
        CaseData caseData = callbackParams.getCaseData();
        var builder = caseData.toBuilder();
        try {
            FeeDto feeDto = feesService.getFeeDataByClaimValue(caseData.getClaimValue());
            builder.claimFee(ClaimFee.builder()
                                 .feeAmount(poundsToPennies(feeDto.getCalculatedAmount()))
                                 .code(feeDto.getCode())
                                 .description(feeDto.getDescription())
                                 .version(feeDto.getVersion())
                                 .build());
        } catch (Exception e) {
            log.error(String.format("Error when calculating fee for case: %s, message: %s",
                                    caseData.getCcdCaseReference(), e.getMessage()
            ));
        }

        return AboutToStartOrSubmitCallbackResponse.builder()
            .data(caseDetailsConverter.toMap(builder.build()))
            .build();
    }
}