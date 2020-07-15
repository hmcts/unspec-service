package uk.gov.hmcts.reform.ucmc.handler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.SubmittedCallbackResponse;
import uk.gov.hmcts.reform.ucmc.callback.CallbackParams;
import uk.gov.hmcts.reform.ucmc.callback.CallbackType;
import uk.gov.hmcts.reform.ucmc.validation.RequestExtensionValidator;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.collect.ImmutableMap.of;
import static java.lang.String.format;
import static java.time.LocalDate.now;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.ucmc.helpers.DateFormatHelper.DATE_AT;
import static uk.gov.hmcts.reform.ucmc.helpers.DateFormatHelper.formatLocalDate;

@SpringBootTest(classes = {
    RequestExtensionCallbackHandler.class,
    RequestExtensionValidator.class,
    JacksonAutoConfiguration.class
})
class RequestExtensionCallbackHandlerTest extends BaseCallbackHandlerTest {

    @Autowired
    private RequestExtensionCallbackHandler handler;

    @Test
    void shouldReturnExpectedErrorInMidEventWhenValuesAreInvalid() {
        Map<String, Object> data = new HashMap<>();
        data.put("extensionProposedDeadline", now().minusDays(1));

        CallbackParams params = callbackParamsOf(data, CallbackType.MID);

        AboutToStartOrSubmitCallbackResponse response = (AboutToStartOrSubmitCallbackResponse) handler.handle(params);

        assertThat(response.getErrors())
            .containsOnly("The proposed deadline can't be in the past.");
    }

    @Test
    void shouldReturnNoErrorInMidEventWhenValuesAreValid() {
        Map<String, Object> data = new HashMap<>();
        data.put("extensionProposedDeadline", now().plusDays(14));

        CallbackParams params = callbackParamsOf(data, CallbackType.MID);

        AboutToStartOrSubmitCallbackResponse response = (AboutToStartOrSubmitCallbackResponse) handler.handle(params);

        assertThat(response.getErrors()).isEmpty();
    }

    @Test
    void shouldReturnExpectedSubmittedCallbackResponseObject() {
        LocalDate proposedDeadline = now().plusDays(14);
        CallbackParams params = callbackParamsOf(
            of("extensionProposedDeadline", proposedDeadline, "extensionAlreadyAgreed", "Yes"),
            CallbackType.SUBMITTED
        );

        SubmittedCallbackResponse response = (SubmittedCallbackResponse) handler.handle(params);

        LocalDate responseDeadline = now().plusDays(7);
        String body = format(
            "<br /><p>You asked if you can respond before 4pm on %s We'll ask the claimant's legal representative"
                + " and email you to tell you whether they accept or reject your request.</p>"
                + "<p>They can choose not to respond to your request, so if you don't get an email from us, "
                + "assume you need to respond before 4pm on %s.</p>",
            formatLocalDate(proposedDeadline, DATE_AT),
            formatLocalDate(responseDeadline, DATE_AT)
        );

        assertThat(response).isEqualToComparingFieldByField(
            SubmittedCallbackResponse.builder()
                .confirmationHeader("# You asked for extra time to respond\n## Claim number: TBC")
                .confirmationBody(body)
                .build());
    }
}
