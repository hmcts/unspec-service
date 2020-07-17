package uk.gov.hmcts.reform.unspec.handler;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.SubmittedCallbackResponse;
import uk.gov.hmcts.reform.unspec.callback.CallbackParams;
import uk.gov.hmcts.reform.unspec.callback.CallbackType;
import uk.gov.hmcts.reform.unspec.validation.RequestExtensionValidator;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.collect.ImmutableMap.of;
import static java.lang.String.format;
import static java.time.LocalDate.now;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.unspec.handler.RequestExtensionCallbackHandler.ALREADY_AGREED;
import static uk.gov.hmcts.reform.unspec.handler.RequestExtensionCallbackHandler.NOT_AGREED;
import static uk.gov.hmcts.reform.unspec.helpers.DateFormatHelper.DATE;
import static uk.gov.hmcts.reform.unspec.helpers.DateFormatHelper.formatLocalDate;

@SpringBootTest(classes = {
    RequestExtensionCallbackHandler.class,
    RequestExtensionValidator.class,
    JacksonAutoConfiguration.class
})
class RequestExtensionCallbackHandlerTest extends BaseCallbackHandlerTest {

    @Autowired
    private RequestExtensionCallbackHandler handler;

    @Nested
    class AboutToSubmitCallback {
        @Test
        void shouldReturnError_whenExtensionIsAlreadyRequested() {
            Map<String, Object> data = new HashMap<>();
            data.put("extensionProposedDeadline", now());

            CallbackParams params = callbackParamsOf(data, CallbackType.ABOUT_TO_START);

            AboutToStartOrSubmitCallbackResponse response = (AboutToStartOrSubmitCallbackResponse) handler
                .handle(params);

            assertThat(response.getErrors())
                .containsOnly("A request for extension can only be requested once.");
        }

        @Test
        void shouldReturnNoError_WhenExtensionIsRequestedFirstTime() {

            CallbackParams params = callbackParamsOf(emptyMap(), CallbackType.ABOUT_TO_START);

            AboutToStartOrSubmitCallbackResponse response = (AboutToStartOrSubmitCallbackResponse) handler
                .handle(params);

            assertThat(response.getErrors()).isEmpty();
        }
    }

    @Nested
    class MidCallback {
        @Test
        void shouldReturnExpectedError_whenValuesAreInvalid() {

            CallbackParams params = callbackParamsOf(
                of("extensionProposedDeadline", now().minusDays(1),
                   "responseDeadline", now().atTime(16, 0)
                ),
                CallbackType.MID
            );

            AboutToStartOrSubmitCallbackResponse response = (AboutToStartOrSubmitCallbackResponse) handler
                .handle(params);

            assertThat(response.getErrors())
                .containsAll(asList(
                    "The proposed deadline must be a future date.",
                    "The proposed deadline can't be before the current response deadline."
                ));
        }

        @Test
        void shouldReturnNoError_whenValuesAreValid() {

            CallbackParams params = callbackParamsOf(
                of("extensionProposedDeadline", now().plusDays(14),
                   "responseDeadline", now().atTime(16, 0)
                ),
                CallbackType.MID
            );

            AboutToStartOrSubmitCallbackResponse response = (AboutToStartOrSubmitCallbackResponse) handler
                .handle(params);

            assertThat(response.getErrors()).isEmpty();
        }
    }

    @Nested
    class SubmittedCallback {
        @Test
        void shouldReturnExpectedResponse_whenAlreadyAgreed() {
            LocalDate proposedDeadline = now().plusDays(14);
            CallbackParams params = callbackParamsOf(
                of("extensionProposedDeadline", proposedDeadline, "extensionAlreadyAgreed", "Yes"),
                CallbackType.SUBMITTED
            );

            SubmittedCallbackResponse response = (SubmittedCallbackResponse) handler.handle(params);

            assertThat(response).isEqualToComparingFieldByField(
                SubmittedCallbackResponse.builder()
                    .confirmationHeader("# You asked for extra time to respond\n## Claim number: TBC")
                    .confirmationBody(prepareBody(proposedDeadline, ALREADY_AGREED))
                    .build());
        }

        @Test
        void shouldReturnExpectedResponse_whenNotAlreadyAgreed() {
            LocalDate proposedDeadline = now().plusDays(14);
            CallbackParams params = callbackParamsOf(
                of("extensionProposedDeadline", proposedDeadline, "extensionAlreadyAgreed", "No"),
                CallbackType.SUBMITTED
            );

            SubmittedCallbackResponse response = (SubmittedCallbackResponse) handler.handle(params);

            assertThat(response).isEqualToComparingFieldByField(
                SubmittedCallbackResponse.builder()
                    .confirmationHeader("# You asked for extra time to respond\n## Claim number: TBC")
                    .confirmationBody(prepareBody(proposedDeadline, NOT_AGREED))
                    .build());
        }

        private String prepareBody(LocalDate proposedDeadline, String notAgreed) {
            LocalDate responseDeadline = now().plusDays(7);
            return format(
                "<br /><p>You asked if you can respond before 4pm on %s %s"
                    + "<p>They can choose not to respond to your request, so if you don't get an email from us, "
                    + "assume you need to respond before 4pm on %s.</p>",
                formatLocalDate(proposedDeadline, DATE),
                notAgreed,
                formatLocalDate(responseDeadline, DATE)
            );
        }
    }
}
