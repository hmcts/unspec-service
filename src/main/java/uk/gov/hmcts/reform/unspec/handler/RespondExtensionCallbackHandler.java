package uk.gov.hmcts.reform.unspec.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.CallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.ccd.client.model.SubmittedCallbackResponse;
import uk.gov.hmcts.reform.unspec.callback.Callback;
import uk.gov.hmcts.reform.unspec.callback.CallbackHandler;
import uk.gov.hmcts.reform.unspec.callback.CallbackParams;
import uk.gov.hmcts.reform.unspec.callback.CallbackType;
import uk.gov.hmcts.reform.unspec.callback.CaseEvent;
import uk.gov.hmcts.reform.unspec.validation.RequestExtensionValidator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static uk.gov.hmcts.reform.unspec.callback.CaseEvent.RESPOND_EXTENSION;
import static uk.gov.hmcts.reform.unspec.helpers.DateFormatHelper.DATE;
import static uk.gov.hmcts.reform.unspec.helpers.DateFormatHelper.formatLocalDateTime;

@Service
public class RespondExtensionCallbackHandler extends CallbackHandler {
    private static final List<CaseEvent> EVENTS = Collections.singletonList(RESPOND_EXTENSION);

    private final ObjectMapper mapper;
    private final RequestExtensionValidator validator;

    public RespondExtensionCallbackHandler(ObjectMapper mapper, RequestExtensionValidator validator) {
        this.mapper = mapper;
        this.validator = validator;
    }

    @Override
    protected Map<CallbackType, Callback> callbacks() {
        return Map.of(
            CallbackType.ABOUT_TO_START, this::prepopulateRequestReasonIfAbsent,
            CallbackType.MID, this::validateRequestedDeadline,
            CallbackType.ABOUT_TO_SUBMIT, this::updateResponseDeadline,
            CallbackType.SUBMITTED, this::buildConfirmation
        );
    }

    @Override
    public List<CaseEvent> handledEvents() {
        return EVENTS;
    }

    private CallbackResponse prepopulateRequestReasonIfAbsent(CallbackParams callbackParams) {
        Map<String, Object> data = callbackParams.getRequest().getCaseDetails().getData();
        data.putIfAbsent("extensionReason", "No reason given");

        return AboutToStartOrSubmitCallbackResponse.builder()
                   .data(data)
                   .build();
    }

    private CallbackResponse validateRequestedDeadline(CallbackParams callbackParams) {
        CaseDetails caseDetails = callbackParams.getRequest().getCaseDetails();

        LocalDate extensionCounterDate = mapper.convertValue(
            caseDetails.getData().get("extensionCounterDate"),
            LocalDate.class
        );

        LocalDateTime responseDeadline = mapper.convertValue(
            caseDetails.getData().get("responseDeadline"),
            LocalDateTime.class
        );

        return AboutToStartOrSubmitCallbackResponse.builder()
                   .errors(validator.validateProposedDeadline(extensionCounterDate, responseDeadline))
                   .build();
    }

    private CallbackResponse updateResponseDeadline(CallbackParams callbackParams) {
        Map<String, Object> data = callbackParams.getRequest().getCaseDetails().getData();

        if (data.get("extensionCounterDate") != null) {
            LocalDate newDeadline = mapper.convertValue(data.get("extensionCounterDate"), LocalDate.class);
            data.put("responseDeadline", newDeadline.atTime(16, 0));
        }

        if (data.get("extensionProposedDeadline") != null) {
            LocalDate newDeadline = mapper.convertValue(data.get("extensionProposedDeadline"), LocalDate.class);
            data.put("responseDeadline", newDeadline.atTime(16, 0));
        }

        return AboutToStartOrSubmitCallbackResponse.builder()
                   .data(data)
                   .build();
    }


    private SubmittedCallbackResponse buildConfirmation(CallbackParams callbackParams) {
        Map<String, Object> data = callbackParams.getRequest().getCaseDetails().getData();
        LocalDateTime responseDeadline = mapper.convertValue(
            data.get("responseDeadline"), LocalDateTime.class
        );

        String claimNumber = "TBC";

        String body = format(
            "<br />The defendant must respond before 4pm on %s", formatLocalDateTime(responseDeadline, DATE));

        return SubmittedCallbackResponse.builder()
                   .confirmationHeader(format("# You've responded to the request for more time\n## Claim number: %s",
                       claimNumber))
                   .confirmationBody(body)
                   .build();
    }
}
