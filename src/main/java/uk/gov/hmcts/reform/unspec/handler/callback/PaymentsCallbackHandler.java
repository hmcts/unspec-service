package uk.gov.hmcts.reform.unspec.handler.callback;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.CallbackResponse;
import uk.gov.hmcts.reform.payments.client.models.PaymentDto;
import uk.gov.hmcts.reform.unspec.callback.Callback;
import uk.gov.hmcts.reform.unspec.callback.CallbackHandler;
import uk.gov.hmcts.reform.unspec.callback.CallbackParams;
import uk.gov.hmcts.reform.unspec.callback.CaseEvent;
import uk.gov.hmcts.reform.unspec.config.PaymentsConfiguration;
import uk.gov.hmcts.reform.unspec.helpers.CaseDetailsConverter;
import uk.gov.hmcts.reform.unspec.model.CaseData;
import uk.gov.hmcts.reform.unspec.service.PaymentsService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static uk.gov.hmcts.reform.unspec.callback.CallbackType.ABOUT_TO_SUBMIT;
import static uk.gov.hmcts.reform.unspec.callback.CaseEvent.MAKE_PBA_PAYMENT;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentsCallbackHandler extends CallbackHandler {

    private static final List<CaseEvent> EVENTS = Collections.singletonList(MAKE_PBA_PAYMENT);
    private static final String ERROR_MESSAGE = "An error occurred";

    private final CaseDetailsConverter caseDetailsConverter;
    private final PaymentsConfiguration paymentsConfiguration;
    private final PaymentsService paymentsService;
    private final ObjectMapper objectMapper;

    @Override
    protected Map<String, Callback> callbacks() {
        return Map.of(callbackKey(ABOUT_TO_SUBMIT), this::makePbaPayment);
    }

    @Override
    public List<CaseEvent> handledEvents() {
        return EVENTS;
    }

    private CallbackResponse makePbaPayment(CallbackParams callbackParams) {
        var caseData = callbackParams.getCaseData();
        List<String> errors = new ArrayList<>();
        if (paymentsConfiguration.isEnabled()) {
            try {
                PaymentDto paymentDto = paymentsService.createCreditAccountPayment(caseData);
                caseData = caseData.toBuilder()
                    .paymentReference(paymentDto.getReference())
                    .paymentFailureReason(null)
                    .build();

            } catch (FeignException e) {
                log.error(String.format("Error when making payment for case: %s, message: %s",
                                        caseData.getCcdCaseReference(), e.getMessage()));
                switch (e.status()) {
                    case 403:
                    case 404:
                    case 422:
                        caseData = CaseData.builder().paymentFailureReason(handleBusinessException(e)).build();
                        break;
                    default:
                        errors.add(ERROR_MESSAGE);
                        break;
                }
            }
        }

        return AboutToStartOrSubmitCallbackResponse.builder()
            .data(caseDetailsConverter.toMap(caseData))
            .errors(errors)
            .build();
    }

    private String handleBusinessException(FeignException e) {
        try {
            var paymentDto = objectMapper.readValue(e.contentUTF8(), PaymentDto.class);
            return paymentDto.getStatusHistories()[0].getErrorMessage();
        } catch (JsonProcessingException jsonException) {
            return e.contentUTF8();
        }
    }
}
