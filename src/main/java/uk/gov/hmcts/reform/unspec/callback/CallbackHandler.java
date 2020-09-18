package uk.gov.hmcts.reform.unspec.callback;

import uk.gov.hmcts.reform.ccd.client.model.CallbackResponse;
import uk.gov.hmcts.reform.unspec.model.BusinessProcess;
import uk.gov.hmcts.reform.unspec.model.CaseData;

import java.util.List;
import java.util.Map;

import static java.util.Optional.ofNullable;

public abstract class CallbackHandler {

    public static final String DEFAULT = "default";

    protected abstract Map<CallbackType, Callback> callbacks();

    public abstract List<CaseEvent> handledEvents();

    public String camundaTaskId() {
        return DEFAULT;
    }

    public boolean isEventAlreadyProcessed(CaseData caseData) {
        if (camundaTaskId().equals(DEFAULT)) {

            return false;
        }
        BusinessProcess businessProcess = caseData.getBusinessProcess();

        return businessProcess != null && camundaTaskId().equals(businessProcess.getTaskId());
    }

    public void register(Map<String, CallbackHandler> handlers) {
        handledEvents().forEach(
            handledEvent -> handlers.put(handledEvent.getValue(), this));
    }

    public CallbackResponse handle(CallbackParams callbackParams) {
        return ofNullable(callbacks().get(callbackParams.getType()))
            .map(callback -> callback.execute(callbackParams))
            .orElseThrow(() -> new CallbackException(
                String.format(
                    "Callback for event %s, type %s not implemented",
                    callbackParams.getRequest().getEventId(),
                    callbackParams.getType()
                )));
    }
}
