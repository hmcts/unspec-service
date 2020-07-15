package uk.gov.hmcts.reform.ucmc.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.CallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.SubmittedCallbackResponse;
import uk.gov.hmcts.reform.ucmc.callback.Callback;
import uk.gov.hmcts.reform.ucmc.callback.CallbackHandler;
import uk.gov.hmcts.reform.ucmc.callback.CallbackParams;
import uk.gov.hmcts.reform.ucmc.callback.CallbackType;
import uk.gov.hmcts.reform.ucmc.callback.CaseEvent;
import uk.gov.hmcts.reform.ucmc.validation.RequestExtensionValidator;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.time.LocalDate.now;
import static uk.gov.hmcts.reform.ucmc.callback.CaseEvent.REQUEST_EXTENSION;
import static uk.gov.hmcts.reform.ucmc.helpers.DateFormatHelper.DATE_AT;
import static uk.gov.hmcts.reform.ucmc.helpers.DateFormatHelper.formatLocalDate;

@Slf4j
@Service
public class RequestExtensionCallbackHandler extends CallbackHandler {
    private static final List<CaseEvent> EVENTS = Collections.singletonList(REQUEST_EXTENSION);

    private final ObjectMapper mapper;
    private final RequestExtensionValidator validator;

    public RequestExtensionCallbackHandler(ObjectMapper mapper, RequestExtensionValidator validator) {
        this.mapper = mapper;
        this.validator = validator;
    }

    @Override
    protected Map<CallbackType, Callback> callbacks() {
        return Map.of(
            CallbackType.MID, this::validateRequestedDeadline,
            CallbackType.SUBMITTED, this::buildConfirmation
        );
    }

    @Override
    public List<CaseEvent> handledEvents() {
        return EVENTS;
    }

    private CallbackResponse validateRequestedDeadline(CallbackParams callbackParams) {
        Map<String, Object> data = callbackParams.getRequest().getCaseDetails().getData();

        return AboutToStartOrSubmitCallbackResponse.builder()
            .data(data)
            .errors(validator.validate(callbackParams.getRequest()))
            .build();
    }

    private SubmittedCallbackResponse buildConfirmation(CallbackParams callbackParams) {
        Map<String, Object> data = callbackParams.getRequest().getCaseDetails().getData();
        LocalDate proposedDeadline = mapper.convertValue(
            data.get("extensionProposedDeadline"),
            LocalDate.class
        );
        String claimNumber = "TBC";

        LocalDate responseDeadline = now().plusDays(14);
        String body = format(
            "<br /><p>You asked if you can respond before 4pm on %s We'll ask the claimant's legal representative"
                + " and email you to tell you whether they accept or reject your request.</p>"
                + "<p>They can choose not to respond to your request, so if you don't get an email from us, "
                + "assume you need to respond before 4pm on %s.</p>",
            formatLocalDate(proposedDeadline, DATE_AT),
            formatLocalDate(responseDeadline, DATE_AT)
        );

        return SubmittedCallbackResponse.builder()
            .confirmationHeader(format("# You asked for extra time to respond\n## Claim number: %s", claimNumber))
            .confirmationBody(body)
            .build();
    }
}
