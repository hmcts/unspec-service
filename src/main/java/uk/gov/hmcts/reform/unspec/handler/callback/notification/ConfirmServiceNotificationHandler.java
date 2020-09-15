package uk.gov.hmcts.reform.unspec.handler.callback.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.CallbackResponse;
import uk.gov.hmcts.reform.unspec.callback.*;
import uk.gov.hmcts.reform.unspec.config.properties.notification.NotificationsProperties;
import uk.gov.hmcts.reform.unspec.helpers.CaseDetailsConverter;
import uk.gov.hmcts.reform.unspec.model.CaseData;
import uk.gov.hmcts.reform.unspec.service.NotificationService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static uk.gov.hmcts.reform.unspec.callback.CaseEvent.*;
import static uk.gov.hmcts.reform.unspec.config.properties.notification.NotificationTemplateParameters.*;
import static uk.gov.hmcts.reform.unspec.helpers.DateFormatHelper.*;
import static uk.gov.hmcts.reform.unspec.utils.PartyNameUtils.getPartyNameBasedOnType;

@Service
@RequiredArgsConstructor
public class ConfirmServiceNotificationHandler extends CallbackHandler {
    private static final List<CaseEvent> EVENTS
        = List.of(SEND_SEALED_CLAIM_EMAIL, GENERATE_BPA_PAYMENT,
                  CALCULATE_CLAIM_FEE, GENERATE_SEALED_CLAIM
    );

    private final NotificationService notificationService;
    private final NotificationsProperties notificationsProperties;
    private final CaseDetailsConverter caseDetailsConverter;

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
        notificationService.sendMail(
            "dharmendrak02@gmail.com",
            notificationsProperties.getEmailTemplates().getClaimantClaimIssued(),
            addProperties(callbackParams),
            "some reference"
        );
        return AboutToStartOrSubmitCallbackResponse.builder().build();
    }

    private Map<String, String> addProperties(CallbackParams callbackParams) {
        CaseData caseData = caseDetailsConverter.toCaseData(callbackParams.getRequest().getCaseDetails());
        return Map.of(
            CLAIM_REFERENCE_NUMBER, caseData.getLegacyCaseReference(),
            DEFENDANT_SOLICITOR_NAME, getPartyNameBasedOnType(caseData.getApplicant1()),
            DEFENDANT_NAME, getPartyNameBasedOnType(caseData.getApplicant1()),
            CLAIMANT_NAME, getPartyNameBasedOnType(caseData.getApplicant1()),
            ISSUED_ON, formatLocalDate(caseData.getClaimIssuedDate(), DATE),
            RESPONSE_DEADLINE, formatLocalDateTime(caseData.getRespondentSolicitor1ResponseDeadline(), DATE_TIME_AT)

        );
    }
}
