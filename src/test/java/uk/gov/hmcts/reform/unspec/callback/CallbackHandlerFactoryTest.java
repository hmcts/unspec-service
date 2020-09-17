package uk.gov.hmcts.reform.unspec.callback;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.CallbackRequest;
import uk.gov.hmcts.reform.ccd.client.model.CallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.unspec.helpers.CaseDetailsConverter;
import uk.gov.hmcts.reform.unspec.model.BusinessProcess;
import uk.gov.hmcts.reform.unspec.model.CaseData;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static uk.gov.hmcts.reform.unspec.callback.CallbackType.ABOUT_TO_SUBMIT;
import static uk.gov.hmcts.reform.unspec.callback.CallbackVersion.V_1;
import static uk.gov.hmcts.reform.unspec.callback.CallbackVersion.V_2;
import static uk.gov.hmcts.reform.unspec.callback.CaseEvent.CREATE_CASE;
import static uk.gov.hmcts.reform.unspec.callback.CaseEvent.NOTIFY_DEFENDANT_SOLICITOR_FOR_CLAIM_ISSUE;

@ExtendWith(MockitoExtension.class)
class CallbackHandlerFactoryTest {

    public static final String BEARER_TOKEN = "Bearer Token";
    public static final CallbackResponse RESPONSE = AboutToStartOrSubmitCallbackResponse.builder().build();

    private CallbackHandler createCaseCallbackHandler = new CallbackHandler() {
        @Override
        protected Map<CallbackType, Callback> callbacks() {
            return ImmutableMap.of(
                ABOUT_TO_SUBMIT, this::createCitizenClaim
            );
        }

        private CallbackResponse createCitizenClaim(CallbackParams callbackParams) {
            return RESPONSE;
        }

        @Override
        public List<CaseEvent> handledEvents() {
            return Collections.singletonList(CREATE_CASE);
        }
    };

    private CallbackHandler sendSealedClaimCallbackHandler = new CallbackHandler() {
        @Override
        protected Map<CallbackType, Callback> callbacks() {
            return ImmutableMap.of(
                ABOUT_TO_SUBMIT, this::sendSealedClaim
            );
        }

        private CallbackResponse sendSealedClaim(CallbackParams callbackParams) {
            return RESPONSE;
        }

        @Override
        public String camundaTaskId() {
            return "SealedClaimEmailTaskId";
        }

        @Override
        public List<CaseEvent> handledEvents() {
            return Collections.singletonList(NOTIFY_DEFENDANT_SOLICITOR_FOR_CLAIM_ISSUE);
        }
    };

    private CallbackHandlerFactory callbackHandlerFactory;

    @Mock
    private CaseDetailsConverter caseDetailsConverter;

    @BeforeEach
    void setUp() {
        callbackHandlerFactory = new CallbackHandlerFactory(caseDetailsConverter, createCaseCallbackHandler,
                                                            sendSealedClaimCallbackHandler);
    }

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
    void shouldDispatchCallback_whenValidCaseEvent() {
        CallbackRequest callbackRequest = CallbackRequest
            .builder()
            .eventId(CREATE_CASE.getValue())
            .build();
        CallbackParams params = CallbackParams.builder()
            .request(callbackRequest)
            .type(ABOUT_TO_SUBMIT)
            .version(V_1)
            .params(ImmutableMap.of(CallbackParams.Params.BEARER_TOKEN, BEARER_TOKEN))
            .build();

        CallbackResponse callbackResponse = callbackHandlerFactory.dispatch(params);

        assertEquals(RESPONSE, callbackResponse);
    }

    @Test
    void shouldBeTrue_whenEventIsAlreadyProcessed() {
        CallbackRequest callbackRequest = CallbackRequest
            .builder()
            .eventId(NOTIFY_DEFENDANT_SOLICITOR_FOR_CLAIM_ISSUE.getValue())
            .caseDetails(CaseDetails.builder().build())
            .build();

        given(caseDetailsConverter.toCaseData(any(CaseDetails.class)))
            .willReturn(CaseData.builder()
                            .businessProcess(BusinessProcess.builder().taskId("SealedClaimEmailTaskId").build())
                            .build());

        CallbackParams params = CallbackParams.builder()
            .request(callbackRequest)
            .type(ABOUT_TO_SUBMIT)
            .version(V_1)
            .params(ImmutableMap.of(CallbackParams.Params.BEARER_TOKEN, BEARER_TOKEN))
            .build();

        assertTrue(callbackHandlerFactory.isEventAlreadyProcessed(params));
    }

    @Test
    void shouldBeFalse_whenEventIsNotAlreadyProcessed() {
        CallbackRequest callbackRequest = CallbackRequest
            .builder()
            .eventId(NOTIFY_DEFENDANT_SOLICITOR_FOR_CLAIM_ISSUE.getValue())
            .caseDetails(CaseDetails.builder().build())
            .build();

        given(caseDetailsConverter.toCaseData(any(CaseDetails.class)))
            .willReturn(CaseData.builder()
                            .businessProcess(BusinessProcess.builder().taskId("unProcessedTask").build())
                            .build());

        CallbackParams params = CallbackParams.builder()
            .request(callbackRequest)
            .type(ABOUT_TO_SUBMIT)
            .version(V_1)
            .params(ImmutableMap.of(CallbackParams.Params.BEARER_TOKEN, BEARER_TOKEN))
            .build();

        assertFalse(callbackHandlerFactory.isEventAlreadyProcessed(params));
    }

    @Test
    void shouldBeFalse_whenEventHasNoCamundaTask() {
        CallbackRequest callbackRequest = CallbackRequest
            .builder()
            .eventId(CREATE_CASE.getValue())
            .caseDetails(CaseDetails.builder().build())
            .build();

        given(caseDetailsConverter.toCaseData(any(CaseDetails.class)))
            .willReturn(CaseData.builder()
                            .businessProcess(BusinessProcess.builder().taskId("unProcessedTask").build())
                            .build());

        CallbackParams params = CallbackParams.builder()
            .request(callbackRequest)
            .type(ABOUT_TO_SUBMIT)
            .version(V_1)
            .params(ImmutableMap.of(CallbackParams.Params.BEARER_TOKEN, BEARER_TOKEN))
            .build();

        assertFalse(callbackHandlerFactory.isEventAlreadyProcessed(params));
    }
}
