package uk.gov.hmcts.reform.unspec.handler.callback.camunda.notification;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import uk.gov.hmcts.reform.unspec.callback.CallbackParams;
import uk.gov.hmcts.reform.unspec.config.properties.notification.NotificationsProperties;
import uk.gov.hmcts.reform.unspec.handler.callback.BaseCallbackHandlerTest;
import uk.gov.hmcts.reform.unspec.model.CaseData;
import uk.gov.hmcts.reform.unspec.sampledata.CallbackParamsBuilder;
import uk.gov.hmcts.reform.unspec.sampledata.CaseDataBuilder;
import uk.gov.hmcts.reform.unspec.service.NotificationService;

import java.util.Map;

import static org.mockito.Mockito.verify;
import static uk.gov.hmcts.reform.unspec.callback.CallbackType.ABOUT_TO_SUBMIT;
import static uk.gov.hmcts.reform.unspec.helpers.DateFormatHelper.DATE;
import static uk.gov.hmcts.reform.unspec.helpers.DateFormatHelper.formatLocalDate;
import static uk.gov.hmcts.reform.unspec.sampledata.CaseDataBuilder.CLAIM_ISSUED_DATE;
import static uk.gov.hmcts.reform.unspec.sampledata.ServiceMethodBuilder.SERVICE_EMAIL;

@SpringBootTest(classes = {
    CreateClaimRespondentNotificationHandler.class,
    NotificationsProperties.class,
    JacksonAutoConfiguration.class
})
class CreateClaimRespondentNotificationHandlerTest extends BaseCallbackHandlerTest {

    @MockBean
    private NotificationService notificationService;
    @Autowired
    private NotificationsProperties notificationsProperties;

    @Autowired
    private CreateClaimRespondentNotificationHandler handler;

    @Nested
    class AboutToSubmitCallback {

        @Test
        void shouldNotifyRespondentSolicitor_whenInvoked() {
            CaseData caseData = CaseDataBuilder.builder().atStateRespondedToClaim().build();
            CallbackParams params = CallbackParamsBuilder.builder().of(ABOUT_TO_SUBMIT, caseData).build();

            handler.handle(params);

            verify(notificationService).sendMail(
                SERVICE_EMAIL,
                notificationsProperties.getRespondentSolicitorClaimIssueEmailTemplate(),
                getExpectedMap(),
                "create-claim-respondent-notification-000LR001"
            );
        }

        private Map<String, String> getExpectedMap() {
            return Map.of(
                "claimReferenceNumber", "000LR001",
                "defendantSolicitorName", "Placeholder name",
                "claimantName", "Mr. John Rambo",
                "defendantName", "Mr. Sole Trader",
                "issuedOn", formatLocalDate(CLAIM_ISSUED_DATE, DATE)
            );
        }
    }
}