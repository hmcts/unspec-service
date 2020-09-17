package uk.gov.hmcts.reform.unspec.callback;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.ccd.client.model.CallbackResponse;
import uk.gov.hmcts.reform.unspec.helpers.CaseDetailsConverter;

import java.util.Arrays;
import java.util.HashMap;

import static java.util.Optional.ofNullable;

@Service
public class CallbackHandlerFactory {

    private final HashMap<String, CallbackHandler> eventHandlers = new HashMap<>();
    private final CaseDetailsConverter caseDetailsConverter;

    @Autowired
    public CallbackHandlerFactory(CaseDetailsConverter caseDetailsConverter, CallbackHandler... beans) {
        this.caseDetailsConverter = caseDetailsConverter;
        Arrays.asList(beans).forEach(bean -> bean.register(eventHandlers));
    }

    public CallbackResponse dispatch(CallbackParams callbackParams) {
        String eventId = callbackParams.getRequest().getEventId();
        return ofNullable(eventHandlers.get(eventId))
            .map(h -> h.handle(callbackParams))
            .orElseThrow(() -> new CallbackException("Could not handle callback for event " + eventId));
    }

    public boolean isEventAlreadyProcessed(CallbackParams callbackParams) {
        String eventId = callbackParams.getRequest().getEventId();
        return ofNullable(eventHandlers.get(eventId))
            .filter(h -> h.isEventAlreadyProcessed(caseDetailsConverter
                                                       .toCaseData(callbackParams.getRequest().getCaseDetails())))
            .isPresent();

    }
}
