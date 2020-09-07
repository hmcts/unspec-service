package uk.gov.hmcts.reform.unspec.handler.callback.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.CallbackResponse;
import uk.gov.hmcts.reform.unspec.callback.Callback;
import uk.gov.hmcts.reform.unspec.callback.CallbackHandler;
import uk.gov.hmcts.reform.unspec.callback.CallbackParams;
import uk.gov.hmcts.reform.unspec.callback.CallbackType;
import uk.gov.hmcts.reform.unspec.callback.CaseEvent;
import uk.gov.hmcts.reform.unspec.service.NotificationService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static uk.gov.hmcts.reform.unspec.callback.CaseEvent.CONFIRM_SERVICE_CONFIRMATION_MAIL;
import static uk.gov.hmcts.reform.unspec.callback.CaseEvent.REQUEST_EXTENSION;

@Service
@RequiredArgsConstructor
public class ConfirmServiceNotificationHandler extends CallbackHandler {
    private static final List<CaseEvent> EVENTS = Collections.singletonList(CONFIRM_SERVICE_CONFIRMATION_MAIL);

    private final NotificationService notificationService;

    @Override
    protected Map<CallbackType, Callback> callbacks() {
        return Map.of(
            CallbackType.ABOUT_TO_SUBMIT, this::notifyDefendantForConfirmOfService
        );
    }

    @Override
    public List<CaseEvent> handledEvents() {
        return EVENTS;
    }

    private CallbackResponse notifyDefendantForConfirmOfService(CallbackParams callbackParams) {
        return AboutToStartOrSubmitCallbackResponse.builder().build();
    }
}
