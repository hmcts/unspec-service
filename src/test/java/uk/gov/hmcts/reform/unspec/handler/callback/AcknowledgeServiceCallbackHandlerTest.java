package uk.gov.hmcts.reform.unspec.handler.callback;

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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {AcknowledgeServiceCallbackHandler.class, JacksonAutoConfiguration.class})
class AcknowledgeServiceCallbackHandlerTest extends BaseCallbackHandlerTest {

    @Autowired
    private AcknowledgeServiceCallbackHandler handler;

    @Nested
    class MidEventCallback {

        @ParameterizedTest
        @ValueSource(strings = {"individualDateOfBirth", "soleTraderDateOfBirth"})
        void shouldReturnError_whenDateOfBirthIsInTheFuture(String dateOfBirthField) {
            Map<String, Object> data = new HashMap<>();
            data.put("respondent", Map.of(dateOfBirthField, "2030-01-01"));

            CallbackParams params = callbackParamsOf(data, CallbackType.MID);

            AboutToStartOrSubmitCallbackResponse response = (AboutToStartOrSubmitCallbackResponse) handler
                .handle(params);

            assertThat(response.getErrors()).containsExactly("The date entered cannot be in the future");
        }

        @ParameterizedTest
        @ValueSource(strings = {"individualDateOfBirth", "soleTraderDateOfBirth"})
        void shouldReturnNoError_whenDateOfBirthIsInThePast(String dateOfBirthField) {
            Map<String, Object> data = new HashMap<>();
            data.put("respondent", Map.of(dateOfBirthField, "2000-01-01"));

            CallbackParams params = callbackParamsOf(data, CallbackType.MID);

            AboutToStartOrSubmitCallbackResponse response = (AboutToStartOrSubmitCallbackResponse) handler
                .handle(params);

            assertThat(response.getData()).isEqualTo(data);
            assertThat(response.getErrors()).isEmpty();
        }

        @Test
        void shouldReturnNoError_whenDateOfBirthIsNotProvided() {
            Map<String, Object> data = new HashMap<>();

            CallbackParams params = callbackParamsOf(data, CallbackType.MID);

            AboutToStartOrSubmitCallbackResponse response = (AboutToStartOrSubmitCallbackResponse) handler
                .handle(params);

            assertThat(response.getData()).isEqualTo(data);
            assertThat(response.getErrors()).isEmpty();
        }
    }

    @Nested
    class AboutToSubmitCallback {

        @Test
        void shouldSetNewResponseDeadline_whenInvoked() {
            Map<String, Object> data = new HashMap<>();
            LocalDateTime responseDeadline = now();
            data.put("responseDeadline", responseDeadline);

            CallbackParams params = callbackParamsOf(data, CallbackType.ABOUT_TO_SUBMIT);

            AboutToStartOrSubmitCallbackResponse response = (AboutToStartOrSubmitCallbackResponse) handler
                .handle(params);

            assertThat(response.getData()).isEqualTo(Map.of("responseDeadline", responseDeadline.plusDays(14)));
        }
    }

    @Nested
    class SubmittedCallback {

        @Test
        void shouldReturnExpectedResponse_whenInvoked() {
            Map<String, Object> data = new HashMap<>();
            data.put("responseDeadline", "2030-01-01T16:00:00");

            CallbackParams params = callbackParamsOf(data, CallbackType.SUBMITTED);

            SubmittedCallbackResponse response = (SubmittedCallbackResponse) handler.handle(params);

            assertThat(response).isEqualToComparingFieldByField(
                SubmittedCallbackResponse.builder()
                    .confirmationHeader("# You've acknowledged service")
                    .confirmationBody("<br />You need to respond before 4pm on 1 January 2030."
                                          + "\n\n[Download the Acknowledgement of Service form](http://www.google.com)")
                    .build());
        }
    }
}
