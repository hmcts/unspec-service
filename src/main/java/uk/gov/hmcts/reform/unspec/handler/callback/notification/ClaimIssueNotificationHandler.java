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
import uk.gov.hmcts.reform.unspec.config.properties.notification.NotificationsProperties;
import uk.gov.hmcts.reform.unspec.helpers.CaseDetailsConverter;
import uk.gov.hmcts.reform.unspec.model.CaseData;
import uk.gov.hmcts.reform.unspec.service.NotificationService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static uk.gov.hmcts.reform.unspec.callback.CaseEvent.NOTIFY_DEFENDANT_SOLICITOR_FOR_CLAIM_ISSUE;
import static uk.gov.hmcts.reform.unspec.config.properties.notification.NotificationTemplateParameters.CLAIMANT_NAME;
import static uk.gov.hmcts.reform.unspec.config.properties.notification.NotificationTemplateParameters.CLAIM_REFERENCE_NUMBER;
import static uk.gov.hmcts.reform.unspec.config.properties.notification.NotificationTemplateParameters.DEFENDANT_NAME;
import static uk.gov.hmcts.reform.unspec.config.properties.notification.NotificationTemplateParameters.DEFENDANT_SOLICITOR_NAME;
import static uk.gov.hmcts.reform.unspec.config.properties.notification.NotificationTemplateParameters.ISSUED_ON;
import static uk.gov.hmcts.reform.unspec.config.properties.notification.NotificationTemplateParameters.RESPONSE_DEADLINE;
import static uk.gov.hmcts.reform.unspec.helpers.DateFormatHelper.DATE;
import static uk.gov.hmcts.reform.unspec.helpers.DateFormatHelper.DATE_TIME_AT;
import static uk.gov.hmcts.reform.unspec.helpers.DateFormatHelper.formatLocalDate;
import static uk.gov.hmcts.reform.unspec.helpers.DateFormatHelper.formatLocalDateTime;
import static uk.gov.hmcts.reform.unspec.utils.PartyNameUtils.getPartyNameBasedOnType;

@Service
@RequiredArgsConstructor
public class ClaimIssueNotificationHandler extends CallbackHandler {

    private static final List<CaseEvent> EVENTS = List.of(NOTIFY_DEFENDANT_SOLICITOR_FOR_CLAIM_ISSUE);
    public static final String NOTIFY_DEFENDANT_SOLICITOR_FOR_CLAIM_ISSUE_TASK_ID
        = "NotifyDefendantSolicitorForClaimIssue";

    private final NotificationService notificationService;
    private final NotificationsProperties notificationsProperties;
    private final CaseDetailsConverter caseDetailsConverter;

    @Override
    protected Map<CallbackType, Callback> callbacks() {
        return Map.of(
            CallbackType.ABOUT_TO_SUBMIT, this::notifyDefendantSolicitorForClaimIssue
        );
    }

    @Override
    public String camundaTaskId() {
        return NOTIFY_DEFENDANT_SOLICITOR_FOR_CLAIM_ISSUE_TASK_ID;
    }

    @Override
    public List<CaseEvent> handledEvents() {
        return EVENTS;
    }

    private CallbackResponse notifyDefendantSolicitorForClaimIssue(CallbackParams callbackParams) {
        CaseData caseData = caseDetailsConverter.toCaseData(callbackParams.getRequest().getCaseDetails());

        notificationService.sendMail(
            Optional.ofNullable(caseData.getServiceMethodToRespondentSolicitor1().getEmail())
                .orElse("dharmendra.kumar@hmcts.net"), //TODO need correct email address here
            notificationsProperties.getEmailTemplates().getDefendantSolicitorClaimIssued(),
            addProperties(caseData),
            "defendant-solicitor-issue-notification-" + caseData.getLegacyCaseReference()
        );
        return AboutToStartOrSubmitCallbackResponse.builder().build();
    }

    private Map<String, String> addProperties(CaseData caseData) {
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
