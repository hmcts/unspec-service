package uk.gov.hmcts.reform.unspec.handler.callback;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.CallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.SubmittedCallbackResponse;
import uk.gov.hmcts.reform.unspec.callback.*;
import uk.gov.hmcts.reform.unspec.model.CaseData;
import uk.gov.hmcts.reform.unspec.model.Party;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static java.lang.String.format;
import static java.time.LocalDate.now;
import static uk.gov.hmcts.reform.unspec.callback.CaseEvent.ACKNOWLEDGE_SERVICE;
import static uk.gov.hmcts.reform.unspec.helpers.DateFormatHelper.*;
import static uk.gov.hmcts.reform.unspec.helpers.DateFormatHelper.DATE_TIME_AT;

@Service
@RequiredArgsConstructor
public class AcknowledgeServiceCallbackHandler extends CallbackHandler {

    private static final List<CaseEvent> EVENTS = Collections.singletonList(ACKNOWLEDGE_SERVICE);
    private static final String RESPONDENT = "respondent";
    private static final String RESPONSE_DEADLINE = "responseDeadline";

    private final ObjectMapper mapper;

    @Override
    protected Map<CallbackType, Callback> callbacks() {
        return Map.of(
            CallbackType.MID, this::validateDateOfBirth,
            CallbackType.ABOUT_TO_SUBMIT, this::setNewResponseDeadline,
            CallbackType.SUBMITTED, this::buildConfirmation
        );
    }

    @Override
    public List<CaseEvent> handledEvents() {
        return EVENTS;
    }

    private CallbackResponse validateDateOfBirth(CallbackParams callbackParams) {
        Map<String, Object> data = callbackParams.getRequest().getCaseDetails().getData();
        Party respondent = mapper.convertValue(data.get(RESPONDENT), Party.class);
        List<String> errors = new ArrayList<>();

        //TODO: single place for all dates validation
        Optional.ofNullable(getDateOfBirth(respondent))
            .filter(date -> date.isAfter(now()))
            .ifPresent(bool -> errors.add("The date entered cannot be in the future"));

        return AboutToStartOrSubmitCallbackResponse.builder()
                   .data(data)
                   .errors(errors)
                   .build();
    }
    private CallbackResponse setNewResponseDeadline(CallbackParams callbackParams) {
        Map<String, Object> data = callbackParams.getRequest().getCaseDetails().getData();
        LocalDateTime responseDeadline = mapper.convertValue(data.get(RESPONSE_DEADLINE), LocalDateTime.class);

        //TODO: use working day calculation logic
        data.put(RESPONSE_DEADLINE, responseDeadline.plusDays(14));

        return AboutToStartOrSubmitCallbackResponse.builder()
                   .data(data)
                   .build();
    }

    private SubmittedCallbackResponse buildConfirmation(CallbackParams callbackParams) {
        Map<String, Object> data = callbackParams.getRequest().getCaseDetails().getData();
        LocalDateTime responseDeadline = mapper.convertValue(data.get(RESPONSE_DEADLINE), LocalDateTime.class);

        String formattedDeemedDateOfService = formatLocalDateTime(responseDeadline, DATE);
        String acknowledgmentOfServiceForm = "http://www.google.com";

        String body = format("<br />You need to respond before 4pm on %s."
                                 + "\n\n[Download the Acknowledgement of Service form](%s)",
                             formattedDeemedDateOfService, acknowledgmentOfServiceForm
        );

        return SubmittedCallbackResponse.builder()
            .confirmationHeader("# You've acknowledged service")
            .confirmationBody(body)
            .build();
    }

    private LocalDate getDateOfBirth(Party respondent) {
        return Optional.ofNullable(respondent.getIndividualDateOfBirth())
            .orElse(respondent.getSoleTraderDateOfBirth());
    }
}
