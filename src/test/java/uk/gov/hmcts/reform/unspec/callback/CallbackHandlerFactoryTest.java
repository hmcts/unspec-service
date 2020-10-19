package uk.gov.hmcts.reform.unspec.callback;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.CallbackRequest;
import uk.gov.hmcts.reform.ccd.client.model.CallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.unspec.model.BusinessProcess;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.hmcts.reform.unspec.callback.CallbackType.ABOUT_TO_SUBMIT;
import static uk.gov.hmcts.reform.unspec.callback.CallbackVersion.V_1;
import static uk.gov.hmcts.reform.unspec.callback.CallbackVersion.V_2;
import static uk.gov.hmcts.reform.unspec.callback.CaseEvent.CREATE_CLAIM;
import static uk.gov.hmcts.reform.unspec.callback.CaseEvent.NOTIFY_RESPONDENT_SOLICITOR1_FOR_CLAIM_ISSUE;

@SpringBootTest(classes = {
    CallbackHandlerFactory.class,
    JacksonAutoConfiguration.class},
    properties = {"spring.main.allow-bean-definition-overriding=true"}
)
@Import(CallbackHandlerFactoryTest.OverrideBean.class)
class CallbackHandlerFactoryTest {

    public static final String BEARER_TOKEN = "Bearer Token";
    public static final CallbackResponse EVENT_HANDLED_RESPONSE = AboutToStartOrSubmitCallbackResponse.builder()
        .data(Map.of("state", "created"))
        .build();

    public static final CallbackResponse ALREADY_HANDLED_EVENT_RESPONSE = AboutToStartOrSubmitCallbackResponse.builder()
        .errors(List.of(format("Event %s is already processed", NOTIFY_RESPONDENT_SOLICITOR1_FOR_CLAIM_ISSUE.name())))
        .build();

    @TestConfiguration
    public static class OverrideBean {
        @Bean
        public CallbackHandler createCaseCallbackHandler() {

            return new CallbackHandler() {
                @Override
                protected Map<String, Callback> callbacks() {
                    return ImmutableMap.of(
                        ABOUT_TO_SUBMIT.getValue(), this::createCitizenClaim
                    );
                }

                private CallbackResponse createCitizenClaim(CallbackParams callbackParams) {
                    return EVENT_HANDLED_RESPONSE;
                }

                @Override
                public List<CaseEvent> handledEvents() {
                    return Collections.singletonList(CREATE_CLAIM);
                }
            };
        }

        @Bean
        public CallbackHandler sendSealedClaimCallbackHandler() {

            return new CallbackHandler() {
                @Override
                protected Map<String, Callback> callbacks() {
                    return ImmutableMap.of(
                        ABOUT_TO_SUBMIT.getValue(), this::sendSealedClaim
                    );
                }

                private CallbackResponse sendSealedClaim(CallbackParams callbackParams) {
                    return EVENT_HANDLED_RESPONSE;
                }

                @Override
                public String camundaActivityId() {
                    return "ClaimIssueEmailRespondentSolicitor1";
                }

                @Override
                public List<CaseEvent> handledEvents() {
                    return Collections.singletonList(NOTIFY_RESPONDENT_SOLICITOR1_FOR_CLAIM_ISSUE);
                }
            };
        }
    }

    @Autowired
    private CallbackHandlerFactory callbackHandlerFactory;

    @Test
    void shouldThrowCallbackException_whenUnknownEvent() {
        CallbackRequest callbackRequest = CallbackRequest
            .builder()
            .eventId("nope")
            .build();
        CallbackParams params = CallbackParams.builder()
            .request(callbackRequest)
            .params(ImmutableMap.of(CallbackParams.Params.BEARER_TOKEN, BEARER_TOKEN))
            .version(V_2)
            .build();

        assertThatThrownBy(() -> callbackHandlerFactory.dispatch(params))
            .isInstanceOf(CallbackException.class)
            .hasMessage("Could not handle callback for event nope");
    }

    @Test
    void shouldProcessEvent_whenValidCaseEvent() {
        CallbackRequest callbackRequest = CallbackRequest
            .builder()
            .eventId(CREATE_CLAIM.name())
            .caseDetailsBefore(CaseDetails.builder().data(Map.of(
                "businessProcess",
                BusinessProcess.builder().build()
            )).build())
            .caseDetails(CaseDetails.builder().data(Map.of("state", "created")).build())
            .build();

        CallbackParams params = CallbackParams.builder()
            .request(callbackRequest)
            .type(ABOUT_TO_SUBMIT)
            .version(V_1)
            .params(ImmutableMap.of(CallbackParams.Params.BEARER_TOKEN, BEARER_TOKEN))
            .build();

        CallbackResponse callbackResponse = callbackHandlerFactory.dispatch(params);

        assertEquals(EVENT_HANDLED_RESPONSE, callbackResponse);
    }

    @Test
    void shouldNotProcessEventAgain_whenEventIsAlreadyProcessed() {
        CallbackRequest callbackRequest = CallbackRequest
            .builder()
            .eventId(NOTIFY_RESPONDENT_SOLICITOR1_FOR_CLAIM_ISSUE.name())
            .caseDetailsBefore(CaseDetails.builder().data(Map.of(
                "businessProcess",
                BusinessProcess.builder().activityId("ClaimIssueEmailRespondentSolicitor1").build()
            )).build())
            .caseDetails(CaseDetails.builder().data(Map.of(
                "businessProcess",
                BusinessProcess.builder().activityId("ClaimIssueEmailRespondentSolicitor1").build()
            )).build())
            .build();

        CallbackParams params = CallbackParams.builder()
            .request(callbackRequest)
            .type(ABOUT_TO_SUBMIT)
            .version(V_1)
            .params(ImmutableMap.of(CallbackParams.Params.BEARER_TOKEN, BEARER_TOKEN))
            .build();

        CallbackResponse callbackResponse = callbackHandlerFactory.dispatch(params);

        assertEquals(ALREADY_HANDLED_EVENT_RESPONSE, callbackResponse);
    }

    @Test
    void shouldProcessEvent_whenEventIsNotAlreadyProcessed() {
        CallbackRequest callbackRequest = CallbackRequest
            .builder()
            .eventId(NOTIFY_RESPONDENT_SOLICITOR1_FOR_CLAIM_ISSUE.name())
            .caseDetailsBefore(CaseDetails.builder().data(Map.of(
                "businessProcess",
                BusinessProcess.builder().build()
            )).build())
            .caseDetails(CaseDetails.builder().data(Map.of(
                "businessProcess",
                BusinessProcess.builder().activityId("unProcessedTask").build()
            )).build())
            .build();

        CallbackParams params = CallbackParams.builder()
            .request(callbackRequest)
            .type(ABOUT_TO_SUBMIT)
            .version(V_1)
            .params(ImmutableMap.of(CallbackParams.Params.BEARER_TOKEN, BEARER_TOKEN))
            .build();

        CallbackResponse callbackResponse = callbackHandlerFactory.dispatch(params);

        assertEquals(EVENT_HANDLED_RESPONSE, callbackResponse);
    }

    @Test
    void shouldProcessEvent_whenEventHasNoCamundaActivityId() {
        CallbackRequest callbackRequest = CallbackRequest
            .builder()
            .eventId(CREATE_CLAIM.name())
            .caseDetailsBefore(CaseDetails.builder().data(Map.of(
                "businessProcess",
                BusinessProcess.builder().build()
            )).build())
            .caseDetails(CaseDetails.builder().data(Map.of(
                "businessProcess",
                BusinessProcess.builder().activityId("unProcessedTask").build()
            )).build())
            .build();

        CallbackParams params = CallbackParams.builder()
            .request(callbackRequest)
            .type(ABOUT_TO_SUBMIT)
            .version(V_1)
            .params(ImmutableMap.of(CallbackParams.Params.BEARER_TOKEN, BEARER_TOKEN))
            .build();

        CallbackResponse callbackResponse = callbackHandlerFactory.dispatch(params);

        assertEquals(EVENT_HANDLED_RESPONSE, callbackResponse);
    }
}
