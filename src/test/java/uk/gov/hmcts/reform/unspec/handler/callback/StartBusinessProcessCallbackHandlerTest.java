package uk.gov.hmcts.reform.unspec.handler.callback;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.reform.unspec.callback.CallbackParams;
import uk.gov.hmcts.reform.unspec.callback.CallbackType;
import uk.gov.hmcts.reform.unspec.enums.BusinessProcessStatus;
import uk.gov.hmcts.reform.unspec.helpers.CaseDetailsConverter;
import uk.gov.hmcts.reform.unspec.model.BusinessProcess;
import uk.gov.hmcts.reform.unspec.model.CaseData;
import uk.gov.hmcts.reform.unspec.sampledata.CaseDataBuilder;

import static org.assertj.core.api.Assertions.assertThat;

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

    @ParameterizedTest
    @EnumSource(value = BusinessProcessStatus.class, names = {"READY", "DISPATCHED"})
    void shouldSetStatusStarted_whenInitialStateIs(BusinessProcessStatus status) {
        CaseData caseData = new CaseDataBuilder().atStateClaimDraft()
            .businessProcess(BusinessProcess.builder().status(status).build()).build();

        CallbackParams params
            = callbackParamsOf(caseDetailsConverter.convertToMap(caseData), CallbackType.ABOUT_TO_SUBMIT);

        AboutToStartOrSubmitCallbackResponse response
            = (AboutToStartOrSubmitCallbackResponse) startBusinessProcessCallbackHandler.handle(params);

        CaseData data = caseDetailsConverter.fromMap(response.getData(), CaseData.class);
        BusinessProcess businessProcess = data.getBusinessProcess();
        assertThat(businessProcess.getStatus()).isEqualTo(BusinessProcessStatus.STARTED);
    }

    @ParameterizedTest
    @EnumSource(value = BusinessProcessStatus.class, names = {"STARTED", "FINISHED"})
    void shouldReturnErrors_whenInitialStatusIs(BusinessProcessStatus status) {
        CaseData caseData = new CaseDataBuilder().atStateClaimDraft()
            .businessProcess(BusinessProcess.builder().status(status).build()).build();

        CallbackParams params
            = callbackParamsOf(caseDetailsConverter.convertToMap(caseData), CallbackType.ABOUT_TO_SUBMIT);

        AboutToStartOrSubmitCallbackResponse response
            = (AboutToStartOrSubmitCallbackResponse) startBusinessProcessCallbackHandler.handle(params);

        assertThat(response.getErrors()).contains("Concurrency Error");
    }
}
