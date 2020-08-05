package uk.gov.hmcts.reform.unspec.handler.callback;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.SubmittedCallbackResponse;
import uk.gov.hmcts.reform.unspec.callback.CallbackParams;
import uk.gov.hmcts.reform.unspec.callback.CallbackType;
import uk.gov.hmcts.reform.unspec.enums.ServedDocuments;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {
    ConfirmServiceCallbackHandler.class,
    JacksonAutoConfiguration.class,
    ValidationAutoConfiguration.class
})
class ConfirmServiceCallbackHandlerTest extends BaseCallbackHandlerTest {

    @Autowired
    private ConfirmServiceCallbackHandler handler;

    @Nested
    class AboutToStartCallback {

        @Test
        void shouldPrepopulateServedDocumentsList_whenInvoked() {
            CallbackParams params = callbackParamsOf(new HashMap<>(), CallbackType.ABOUT_TO_START);
            AboutToStartOrSubmitCallbackResponse response = (AboutToStartOrSubmitCallbackResponse) handler
                .handle(params);

            assertThat(response.getData())
                .isEqualTo(Map.of("servedDocuments", List.of(ServedDocuments.CLAIM_FORM)));
        }
    }

    @Nested
    class MidEventCallback {

        @Test
        void shouldReturnError_whenWhitespaceInServedDocumentsOther() {
            Map<String, Object> data = new HashMap<>();
            data.put("servedDocumentsOther", " ");

            CallbackParams params = callbackParamsOf(data, CallbackType.MID);

            AboutToStartOrSubmitCallbackResponse response = (AboutToStartOrSubmitCallbackResponse) handler
                .handle(params);

            assertThat(response.getErrors()).containsExactly(
                "CONTENT TBC: please enter a valid value for other documents");
        }

        @Test
        void shouldReturnNoError_whenValidServedDocumentsOther() {
            Map<String, Object> data = new HashMap<>();
            data.put("servedDocumentsOther", "A valid document");

            CallbackParams params = callbackParamsOf(data, CallbackType.MID);

            AboutToStartOrSubmitCallbackResponse response = (AboutToStartOrSubmitCallbackResponse) handler
                .handle(params);

            assertThat(response.getData()).isEqualTo(data);
            assertThat(response.getErrors()).isEmpty();
        }
    }

    @Nested
    class SecondMidEventCallback {

        private final LocalDate claimIssueDate = LocalDate.of(2000, 6, 22);

        @Nested
        class ServiceDate {

            private final LocalDate today = LocalDate.now();
            private final LocalDate futureDate = today.plusYears(1);

            @Test
            void shouldReturnNoErrors_WhenServiceDateInPastAndAfterIssueDate() {
                Map<String, Object> data = new HashMap<>();
                data.put("serviceMethod", Map.of("type", "POST"));
                data.put("serviceDate", claimIssueDate.plusDays(1));
                data.put("claimIssuedDate", claimIssueDate);

                CallbackParams params = callbackParamsOf(data, CallbackType.MID_SECONDARY);

                AboutToStartOrSubmitCallbackResponse response = (AboutToStartOrSubmitCallbackResponse) handler
                    .handle(params);

                assertThat(response.getErrors()).isEmpty();
            }

            @Test
            void shouldReturnNoErrors_WhenServiceDateIsTodayAndAfterIssueDate() {
                Map<String, Object> data = new HashMap<>();
                data.put("serviceMethod", Map.of("type", "POST"));
                data.put("serviceDate", today);
                data.put("claimIssuedDate", claimIssueDate);

                CallbackParams params = callbackParamsOf(data, CallbackType.MID_SECONDARY);

                AboutToStartOrSubmitCallbackResponse response = (AboutToStartOrSubmitCallbackResponse) handler
                    .handle(params);

                assertThat(response.getErrors()).isEmpty();
            }

            @Test
            void shouldReturnError_WhenServiceDateInFuture() {
                Map<String, Object> data = new HashMap<>();
                data.put("serviceMethod", Map.of("type", "POST"));
                data.put("serviceDate", futureDate);
                data.put("claimIssuedDate", claimIssueDate);

                CallbackParams params = callbackParamsOf(data, CallbackType.MID_SECONDARY);

                AboutToStartOrSubmitCallbackResponse response = (AboutToStartOrSubmitCallbackResponse) handler
                    .handle(params);

                assertThat(response.getErrors()).containsOnly("The date must not be in the future");
            }

            @Test
            void shouldReturnError_WhenServiceDateIsBeforeClaimIssueDate() {
                Map<String, Object> data = new HashMap<>();
                data.put("serviceMethod", Map.of("type", "POST"));
                data.put("serviceDate", claimIssueDate.minusDays(1));
                data.put("claimIssuedDate", claimIssueDate);

                CallbackParams params = callbackParamsOf(data, CallbackType.MID_SECONDARY);

                AboutToStartOrSubmitCallbackResponse response = (AboutToStartOrSubmitCallbackResponse) handler
                    .handle(params);

                assertThat(response.getErrors()).containsOnly("The date must not be before issue date of claim");
            }
        }

        @Nested
        class ServiceDateAndTime {

            private final LocalDateTime today = LocalDateTime.now();
            private final LocalDateTime futureDate = today.plusYears(1);

            @Test
            void shouldReturnNoErrors_WhenServiceDateInPastAndAfterIssueDate() {
                Map<String, Object> data = new HashMap<>();
                data.put("serviceMethod", Map.of("type", "FAX"));
                data.put("serviceDateAndTime", claimIssueDate.plusDays(1).atTime(12, 0));
                data.put("claimIssuedDate", claimIssueDate);

                CallbackParams params = callbackParamsOf(data, CallbackType.MID_SECONDARY);

                AboutToStartOrSubmitCallbackResponse response = (AboutToStartOrSubmitCallbackResponse) handler
                    .handle(params);

                assertThat(response.getErrors()).isEmpty();
            }

            @Test
            void shouldReturnNoErrors_WhenServiceDateIsTodayAndAfterIssueDate() {
                Map<String, Object> data = new HashMap<>();
                data.put("serviceMethod", Map.of("type", "FAX"));
                data.put("serviceDateAndTime", today);
                data.put("claimIssuedDate", claimIssueDate);

                CallbackParams params = callbackParamsOf(data, CallbackType.MID_SECONDARY);

                AboutToStartOrSubmitCallbackResponse response = (AboutToStartOrSubmitCallbackResponse) handler
                    .handle(params);

                assertThat(response.getErrors()).isEmpty();
            }

            @Test
            void shouldReturnError_WhenServiceDateInFuture() {
                Map<String, Object> data = new HashMap<>();
                data.put("serviceMethod", Map.of("type", "FAX"));
                data.put("serviceDateAndTime", futureDate);
                data.put("claimIssuedDate", claimIssueDate);

                CallbackParams params = callbackParamsOf(data, CallbackType.MID_SECONDARY);

                AboutToStartOrSubmitCallbackResponse response = (AboutToStartOrSubmitCallbackResponse) handler
                    .handle(params);

                assertThat(response.getErrors()).containsOnly("The date must not be in the future");
            }

            @Test
            void shouldReturnError_WhenServiceDateIsBeforeClaimIssueDate() {
                Map<String, Object> data = new HashMap<>();
                data.put("serviceMethod", Map.of("type", "FAX"));
                data.put("serviceDateAndTime", claimIssueDate.atTime(12, 0).minusDays(1));
                data.put("claimIssuedDate", claimIssueDate);

                CallbackParams params = callbackParamsOf(data, CallbackType.MID_SECONDARY);

                AboutToStartOrSubmitCallbackResponse response = (AboutToStartOrSubmitCallbackResponse) handler
                    .handle(params);

                assertThat(response.getErrors()).containsOnly("The date must not be before issue date of claim");
            }
        }
    }

    @Nested
    class AboutToSubmitCallback {

        @Test
        void shouldReturnExpectedResponse_whenDateEntry() {
            Map<String, Object> data = new HashMap<>();
            data.put("serviceMethod", Map.of("type", "POST"));
            data.put("serviceDate", "2099-06-23");

            CallbackParams params = callbackParamsOf(data, CallbackType.ABOUT_TO_SUBMIT);

            AboutToStartOrSubmitCallbackResponse response = (AboutToStartOrSubmitCallbackResponse) handler
                .handle(params);

            assertThat(response.getData()).isEqualTo(
                Map.of(
                    "deemedDateOfService", LocalDate.of(2099, 6, 25),
                    "responseDeadline", LocalDateTime.of(2099, 7, 9, 16, 0),
                    "serviceMethod", Map.of("type", "POST"),
                    "serviceDate", "2099-06-23"
                ));
        }

        @Test
        void shouldReturnExpectedResponse_whenDateAndTimeEntry() {
            Map<String, Object> data = new HashMap<>();
            data.put("serviceMethod", Map.of("type", "FAX"));
            data.put("serviceDateAndTime", "2099-06-23T15:00:00");

            CallbackParams params = callbackParamsOf(data, CallbackType.ABOUT_TO_SUBMIT);

            AboutToStartOrSubmitCallbackResponse response = (AboutToStartOrSubmitCallbackResponse) handler
                .handle(params);

            assertThat(response.getData()).isEqualTo(
                Map.of(
                    "deemedDateOfService", LocalDate.of(2099, 6, 23),
                    "responseDeadline", LocalDateTime.of(2099, 7, 7, 16, 0),
                    "serviceMethod", Map.of("type", "FAX"),
                    "serviceDateAndTime", "2099-06-23T15:00:00"
                ));
        }
    }

    @Nested
    class SubmittedCallback {

        @Test
        void shouldReturnExpectedResponse_whenValidData() {
            Map<String, Object> data = new HashMap<>();
            data.put("deemedDateOfService", "2099-06-25");

            CallbackParams params = callbackParamsOf(data, CallbackType.SUBMITTED);

            SubmittedCallbackResponse response = (SubmittedCallbackResponse) handler.handle(params);

            assertThat(response).isEqualToComparingFieldByField(
                SubmittedCallbackResponse.builder()
                    .confirmationHeader("# You've confirmed service")
                    .confirmationBody("<br /> Deemed date of service: 25 June 2099."
                                          + "<br />The defendant must respond before 4:00pm on 9 July 2099."
                                          + "\n\n[Download certificate of service](http://www.google.com) (PDF, 266 KB)")
                    .build());
        }
    }
}
