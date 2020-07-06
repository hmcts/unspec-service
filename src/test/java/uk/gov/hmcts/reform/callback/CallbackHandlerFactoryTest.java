package uk.gov.hmcts.reform.callback;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.CallbackRequest;
import uk.gov.hmcts.reform.ccd.client.model.CallbackResponse;
import uk.gov.hmcts.reform.ucmc.callback.Callback;
import uk.gov.hmcts.reform.ucmc.callback.CallbackException;
import uk.gov.hmcts.reform.ucmc.callback.CallbackHandler;
import uk.gov.hmcts.reform.ucmc.callback.CallbackHandlerFactory;
import uk.gov.hmcts.reform.ucmc.callback.CallbackParams;
import uk.gov.hmcts.reform.ucmc.callback.CallbackType;
import uk.gov.hmcts.reform.ucmc.callback.CaseEvent;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.gov.hmcts.reform.ucmc.callback.CallbackType.ABOUT_TO_SUBMIT;
import static uk.gov.hmcts.reform.ucmc.callback.CaseEvent.CREATE_CASE;

@ExtendWith(MockitoExtension.class)
public class CallbackHandlerFactoryTest {

    public static final String BEARER_TOKEN = "Bearer Token";
    public static final CallbackResponse response = AboutToStartOrSubmitCallbackResponse.builder().build();

    private CallbackHandler sampleCallbackHandler = new CallbackHandler() {
        @Override
        protected Map<CallbackType, Callback> callbacks() {
            return ImmutableMap.of(
                ABOUT_TO_SUBMIT, this::createCitizenClaim
            );
        }

        private CallbackResponse createCitizenClaim(CallbackParams callbackParams) {
            return response;
        }

        @Override
        public List<CaseEvent> handledEvents() {
            return Collections.singletonList(CREATE_CASE);
        }
    };

    private CallbackHandlerFactory callbackHandlerFactory;

    @BeforeEach
    public void setUp() {
        callbackHandlerFactory = new CallbackHandlerFactory(ImmutableList.of(sampleCallbackHandler));
    }

    @Test
    public void shouldThrowIfUnknownEvent() {
        CallbackRequest callbackRequest = CallbackRequest
            .builder()
            .eventId("nope")
            .build();
        CallbackParams params = CallbackParams.builder()
            .request(callbackRequest)
            .params(ImmutableMap.of(CallbackParams.Params.BEARER_TOKEN, BEARER_TOKEN))
            .build();

        Exception exception = assertThrows(
            CallbackException.class,
            () -> callbackHandlerFactory.dispatch(params)
        );

        assertEquals("Could not handle callback for event nope", exception.getMessage());
    }

    @Test
    public void shouldDispatchCallbackForEvent() {

        CallbackRequest callbackRequest = CallbackRequest
            .builder()
            .eventId(CREATE_CASE.getValue())
            .build();
        CallbackParams params = CallbackParams.builder()
            .request(callbackRequest)
            .type(ABOUT_TO_SUBMIT)
            .params(ImmutableMap.of(CallbackParams.Params.BEARER_TOKEN, BEARER_TOKEN))
            .build();

        CallbackResponse callbackResponse = callbackHandlerFactory.dispatch(params);

        assertEquals(response, callbackResponse);
    }

}
