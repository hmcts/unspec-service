package uk.gov.hmcts.reform.unspec.handler.callback;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.CallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.SubmittedCallbackResponse;
import uk.gov.hmcts.reform.unspec.callback.Callback;
import uk.gov.hmcts.reform.unspec.callback.CallbackHandler;
import uk.gov.hmcts.reform.unspec.callback.CallbackParams;
import uk.gov.hmcts.reform.unspec.callback.CaseEvent;
import uk.gov.hmcts.reform.unspec.enums.DefendantResponseType;
import uk.gov.hmcts.reform.unspec.enums.YesOrNo;
import uk.gov.hmcts.reform.unspec.model.BusinessProcess;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static uk.gov.hmcts.reform.unspec.callback.CallbackType.ABOUT_TO_START;
import static uk.gov.hmcts.reform.unspec.callback.CallbackType.ABOUT_TO_SUBMIT;
import static uk.gov.hmcts.reform.unspec.callback.CallbackType.SUBMITTED;
import static uk.gov.hmcts.reform.unspec.callback.CaseEvent.CLAIMANT_RESPONSE;
import static uk.gov.hmcts.reform.unspec.enums.DefendantResponseType.FULL_DEFENCE;
import static uk.gov.hmcts.reform.unspec.enums.YesOrNo.YES;

@Service
@RequiredArgsConstructor
public class RespondToDefenceCallbackHandler extends CallbackHandler {

    private static final List<CaseEvent> EVENTS = Collections.singletonList(CLAIMANT_RESPONSE);
    public static final String APPLICANT_1_PROCEEDING = "applicant1ProceedWithClaim";

    private final ObjectMapper mapper;

    @Override
    public List<CaseEvent> handledEvents() {
        return EVENTS;
    }

    @Override
    protected Map<String, Callback> callbacks() {
        return Map.of(
            callbackKey(ABOUT_TO_START), this::emptyCallbackResponse,
            callbackKey(ABOUT_TO_SUBMIT), this::handleNotifications,
            callbackKey(SUBMITTED), this::buildConfirmation
        );
    }

    private CallbackResponse handleNotifications(CallbackParams callbackParams) {
        Map<String, Object> data = callbackParams.getRequest().getCaseDetails().getData();
        YesOrNo proceeding = mapper.convertValue(data.get(APPLICANT_1_PROCEEDING), YesOrNo.class);
        var response = mapper.convertValue(data.get("respondent1ClaimResponseType"), DefendantResponseType.class);
        if (response == FULL_DEFENCE && proceeding == YES) {
            data.put(
                "businessProcess",
                BusinessProcess.builder().activityId("CaseTransferredToLocalCourtHandling").build()
            );
        }

        return AboutToStartOrSubmitCallbackResponse.builder()
            .data(data)
            .build();
    }

    private SubmittedCallbackResponse buildConfirmation(CallbackParams callbackParams) {
        Map<String, Object> data = callbackParams.getRequest().getCaseDetails().getData();
        YesOrNo proceeding = mapper.convertValue(data.get(APPLICANT_1_PROCEEDING), YesOrNo.class);

        String claimNumber = "TBC";
        String dqLink = "http://www.google.com";

        String body = getBody(proceeding);
        String title = getTitle(proceeding);

        return SubmittedCallbackResponse.builder()
            .confirmationHeader(format(title, claimNumber))
            .confirmationBody(format(body, dqLink))
            .build();
    }

    private String getTitle(YesOrNo proceeding) {
        if (proceeding == YES) {
            return "# You've decided to proceed with the claim%n## Claim number: %s";
        }
        return "# You've decided not to proceed with the claim%n## Claim number: %s";
    }

    private String getBody(YesOrNo proceeding) {
        if (proceeding == YES) {
            return "<br />We'll review the case. We'll contact you to tell you what to do next.%n%n"
                + "[Download directions questionnaire](%s)";
        }
        return "CONTENT TBC";
    }
}
