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
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.unspec.helpers.DateFormatHelper.DATE;
import static uk.gov.hmcts.reform.unspec.helpers.DateFormatHelper.formatLocalDateTime;

@SpringBootTest(classes = {
    RespondExtensionCallbackHandler.class,
    RequestExtensionValidator.class,
    JacksonAutoConfiguration.class
})
class RespondExtensionCallbackHandlerTest extends BaseCallbackHandlerTest {

    @Autowired
    private RespondExtensionCallbackHandler handler;

    @Nested
    class AboutToStartCallback {

        @Test
        void shouldAddNoReasonGiven_WhenNoReasonGivenForExtensionRequest() {
            CallbackParams params = callbackParamsOf(new HashMap<>(), CallbackType.ABOUT_TO_START);

            AboutToStartOrSubmitCallbackResponse response =
                (AboutToStartOrSubmitCallbackResponse) handler.handle(params);

            assertThat(response.getData().get("extensionReason")).isEqualTo("No reason given");
        }

        @Test
        void shouldKeepReasonGiven_WhenReasonGivenForExtensionRequest() {
            Map<String, Object> data = new HashMap<>();
            data.put("extensionReason", "Reason given");
            CallbackParams params = callbackParamsOf(data, CallbackType.ABOUT_TO_START);

            AboutToStartOrSubmitCallbackResponse response =
                (AboutToStartOrSubmitCallbackResponse) handler.handle(params);

            assertThat(response.getData().get("extensionReason")).isEqualTo("Reason given");
        }
    }

    @Nested
    class MidEventCallback {

        @Test
        void shouldReturnExpectedError_whenValuesAreInvalid() {
            CallbackParams params = callbackParamsOf(
                of("extensionCounterDate", now().minusDays(1),
                    "responseDeadline", now().atTime(16, 0)
                ),
                CallbackType.MID
            );

            AboutToStartOrSubmitCallbackResponse response =
                (AboutToStartOrSubmitCallbackResponse) handler.handle(params);

            assertThat(response.getErrors())
                .containsAll(asList(
                    "CONTENT TBC: The proposed deadline must be a future date.",
                    "CONTENT TBC: The proposed deadline can't be before the current response deadline."
                ));
        }

        @Test
        void shouldReturnNoError_whenValuesAreValid() {
            CallbackParams params = callbackParamsOf(
                of("extensionCounterDate", now().plusDays(14),
                    "responseDeadline", now().atTime(16, 0)
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

        @ParameterizedTest(name = "should update response deadline when {0} is set")
        @ValueSource(strings = {"extensionCounterDate", "extensionProposedDeadline"})
        void shouldUpdateResponseDeadline_whenNewDeadline(String deadlineKey) {
            LocalDate proposedDeadline = now().plusDays(14);
            Map<String, Object> map = new HashMap<>();
            map.put(deadlineKey, proposedDeadline);
            map.put("responseDeadline", now().atTime(16, 0));

            CallbackParams params = callbackParamsOf(map, CallbackType.ABOUT_TO_SUBMIT);

            AboutToStartOrSubmitCallbackResponse response =
                (AboutToStartOrSubmitCallbackResponse) handler.handle(params);

            assertThat(response.getData().get("responseDeadline")).isEqualTo(proposedDeadline.atTime(16, 0));
        }

        @Test
        void shouldKeepExistingResponseDeadline_whenNoNewDeadline() {
            LocalDateTime responseDeadline = now().atTime(16, 0);

            Map<String, Object> map = new HashMap<>();
            map.put("responseDeadline", responseDeadline);

            CallbackParams params = callbackParamsOf(map, CallbackType.ABOUT_TO_SUBMIT);

            AboutToStartOrSubmitCallbackResponse response =
                (AboutToStartOrSubmitCallbackResponse) handler.handle(params);

            assertThat(response.getData().get("responseDeadline")).isEqualTo(responseDeadline);
        }
    }

    @Nested
    class SubmittedCallback {

        @Test
        void shouldReturnExpectedResponse_withNewResponseDeadline() {
            LocalDateTime responseDeadline = now().atTime(16, 0);
            CallbackParams params = callbackParamsOf(
                of("responseDeadline", responseDeadline), CallbackType.SUBMITTED
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
