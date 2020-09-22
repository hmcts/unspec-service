package uk.gov.hmcts.reform.unspec.callback;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.CallbackResponse;
import uk.gov.hmcts.reform.unspec.model.BusinessProcess;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.util.Optional.ofNullable;

@Service
public class CallbackHandlerFactory {

    private final HashMap<String, CallbackHandler> eventHandlers = new HashMap<>();
    private final ObjectMapper objectMapper;

    @Autowired
    public CallbackHandlerFactory(ObjectMapper objectMapper, CallbackHandler... beans) {
        this.objectMapper = objectMapper;
        Arrays.asList(beans).forEach(bean -> bean.register(eventHandlers));
    }

    public CallbackResponse dispatch(CallbackParams callbackParams) {
        String eventId = callbackParams.getRequest().getEventId();
        return ofNullable(eventHandlers.get(eventId))
            .map(h -> processEvent(callbackParams, h))
            .orElseThrow(() -> new CallbackException("Could not handle callback for event " + eventId));
    }

    private CallbackResponse processEvent(CallbackParams callbackParams, CallbackHandler handler) {
        Map<String, Object> data = callbackParams.getRequest().getCaseDetails().getData();
        BusinessProcess businessProcess = objectMapper.convertValue(data.get("businessProcess"), BusinessProcess.class);
        return handler.isEventAlreadyProcessed(businessProcess)
            ? AboutToStartOrSubmitCallbackResponse.builder().build()
            : handler.handle(callbackParams);
    }
}
