package uk.gov.hmcts.reform.unspec.handler.callback;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.unspec.callback.CallbackParams;
import uk.gov.hmcts.reform.unspec.enums.BusinessProcessStatus;
import uk.gov.hmcts.reform.unspec.helpers.CaseDetailsConverter;
import uk.gov.hmcts.reform.unspec.model.BusinessProcess;
import uk.gov.hmcts.reform.unspec.model.CaseData;
import uk.gov.hmcts.reform.unspec.sampledata.CallbackParamsBuilder;
import uk.gov.hmcts.reform.unspec.sampledata.CaseDataBuilder;
import uk.gov.hmcts.reform.unspec.sampledata.CaseDetailsBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.unspec.callback.CallbackType.ABOUT_TO_SUBMIT;

@SpringBootTest(classes = {
    StartBusinessProcessCallbackHandler.class,
    JacksonAutoConfiguration.class,
    CaseDetailsConverter.class
})
class StartBusinessProcessCallbackHandlerTest extends BaseCallbackHandlerTest {

    @Autowired
    private StartBusinessProcessCallbackHandler startBusinessProcessCallbackHandler;
    @Autowired
    private CaseDetailsConverter caseDetailsConverter;

    @Nested
    class AboutToStartCallback {

        @Test
        void shouldSetStatusStarted_whenSuccessful() {

            CaseDetails caseDetails = CaseDetailsBuilder.builder()
                .data(new CaseDataBuilder().atStateClaimDraft().build().toBuilder()
                          .businessProcess(BusinessProcess.builder().status(BusinessProcessStatus.READY).build())
                          .build())
                .build();
            CallbackParams params = CallbackParamsBuilder.builder().of(ABOUT_TO_SUBMIT, caseDetails).build();

            AboutToStartOrSubmitCallbackResponse response
                = (AboutToStartOrSubmitCallbackResponse) startBusinessProcessCallbackHandler.handle(params);

            CaseData data = caseDetailsConverter.fromMap(response.getData(), CaseData.class);
            BusinessProcess businessProcess = data.getBusinessProcess();
            assertThat(businessProcess.getStatus()).isEqualTo(BusinessProcessStatus.STARTED);
        }
    }
}
