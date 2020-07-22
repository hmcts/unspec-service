package uk.gov.hmcts.reform.unspec.handler;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.SubmittedCallbackResponse;
import uk.gov.hmcts.reform.unspec.callback.CallbackParams;
import uk.gov.hmcts.reform.unspec.callback.CallbackType;
import uk.gov.hmcts.reform.unspec.validation.RequestExtensionValidator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.collect.ImmutableMap.of;
import static java.lang.String.format;
import static java.time.LocalDate.now;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.unspec.helpers.DateFormatHelper.DATE;
import static uk.gov.hmcts.reform.unspec.helpers.DateFormatHelper.formatLocalDateTime;

@SpringBootTest(classes = {
    RespondExtensionCallbackHandler.class,
    RequestExtensionValidator.class,
    JacksonAutoConfiguration.class
})
class RespondExtensionCallbackHandlerTest extends BaseCallbackHandlerTest {

    public static final String RESPONSE_DEADLINE = "responseDeadline";
    public static final String COUNTER_DATE = "respondentSolicitor1claimResponseExtensionCounterDate";

    @Autowired
    private RespondExtensionCallbackHandler handler;

    @Nested
    class AboutToStartCallback {

        public static final String EXTENSION_REASON = "respondentSolicitor1claimResponseExtensionReason";

        @Test
        void shouldAddNoReasonGiven_WhenNoReasonGivenForExtensionRequest() {
            CallbackParams params = callbackParamsOf(new HashMap<>(), CallbackType.ABOUT_TO_START);

            AboutToStartOrSubmitCallbackResponse response =
                (AboutToStartOrSubmitCallbackResponse) handler.handle(params);

            assertThat(response.getData().get(EXTENSION_REASON)).isEqualTo("No reason given");
        }

        @Test
        void shouldKeepReasonGiven_WhenReasonGivenForExtensionRequest() {
            Map<String, Object> data = new HashMap<>();
            data.put(EXTENSION_REASON, "Reason given");
            CallbackParams params = callbackParamsOf(data, CallbackType.ABOUT_TO_START);

            AboutToStartOrSubmitCallbackResponse response =
                (AboutToStartOrSubmitCallbackResponse) handler.handle(params);

            assertThat(response.getData().get(EXTENSION_REASON)).isEqualTo("Reason given");
        }
    }

    @Nested
    class MidEventCallback {

        @Test
        void shouldReturnExpectedError_whenValuesAreInvalid() {
            CallbackParams params = callbackParamsOf(
                of(COUNTER_DATE, now().minusDays(1),
                    RESPONSE_DEADLINE, now().atTime(16, 0)
                ),
                CallbackType.MID
            );

            AboutToStartOrSubmitCallbackResponse response =
                (AboutToStartOrSubmitCallbackResponse) handler.handle(params);

            assertThat(response.getErrors())
                .containsOnly("The proposed deadline must be a date in the future");
        }

        @Test
        void shouldReturnNoError_whenValuesAreValid() {
            CallbackParams params = callbackParamsOf(
                of(COUNTER_DATE, now().plusDays(14),
                    RESPONSE_DEADLINE, now().atTime(16, 0)
                ),
                CallbackType.MID
            );

            AboutToStartOrSubmitCallbackResponse response =
                (AboutToStartOrSubmitCallbackResponse) handler.handle(params);

            assertThat(response.getErrors()).isEmpty();
        }
    }

    @Nested
    class AboutToSubmitCallback {

        public static final String PROPOSED_DEADLINE = "respondentSolicitor1claimResponseExtensionProposedDeadline";

        @ParameterizedTest(name = "should update response deadline when {0} is set")
        @ValueSource(strings = {COUNTER_DATE, PROPOSED_DEADLINE})
        void shouldUpdateResponseDeadline_whenNewDeadline(String deadlineKey) {
            LocalDate proposedDeadline = now().plusDays(14);
            Map<String, Object> map = new HashMap<>();
            map.put(deadlineKey, proposedDeadline);
            map.put(RESPONSE_DEADLINE, now().atTime(16, 0));

            CallbackParams params = callbackParamsOf(map, CallbackType.ABOUT_TO_SUBMIT);

            AboutToStartOrSubmitCallbackResponse response =
                (AboutToStartOrSubmitCallbackResponse) handler.handle(params);

            assertThat(response.getData().get(RESPONSE_DEADLINE)).isEqualTo(proposedDeadline.atTime(16, 0));
        }

        @Test
        void shouldKeepExistingResponseDeadline_whenNoNewDeadline() {
            LocalDateTime responseDeadline = now().atTime(16, 0);

            Map<String, Object> map = new HashMap<>();
            map.put(RESPONSE_DEADLINE, responseDeadline);

            CallbackParams params = callbackParamsOf(map, CallbackType.ABOUT_TO_SUBMIT);

            AboutToStartOrSubmitCallbackResponse response =
                (AboutToStartOrSubmitCallbackResponse) handler.handle(params);

            assertThat(response.getData().get(RESPONSE_DEADLINE)).isEqualTo(responseDeadline);
        }
    }

    @Nested
    class SubmittedCallback {

        @Test
        void shouldReturnExpectedResponse_withNewResponseDeadline() {
            LocalDateTime responseDeadline = now().atTime(16, 0);
            CallbackParams params = callbackParamsOf(
                of(RESPONSE_DEADLINE, responseDeadline), CallbackType.SUBMITTED
            );

            SubmittedCallbackResponse response = (SubmittedCallbackResponse) handler.handle(params);

            String expectedBody = format(
                "<br />The defendant must respond before 4pm on %s", formatLocalDateTime(responseDeadline, DATE));

            assertThat(response).isEqualToComparingFieldByField(
                SubmittedCallbackResponse.builder()
                    .confirmationHeader("# You've responded to the request for more time\n## Claim number: TBC")
                    .confirmationBody(expectedBody)
                    .build());
        }
    }
}
