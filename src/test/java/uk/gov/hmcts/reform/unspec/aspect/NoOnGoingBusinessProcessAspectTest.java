package uk.gov.hmcts.reform.unspec.aspect;

import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.CallbackRequest;
import uk.gov.hmcts.reform.unspec.callback.CallbackParams;
import uk.gov.hmcts.reform.unspec.helpers.CaseDetailsConverter;
import uk.gov.hmcts.reform.unspec.sampledata.CallbackParamsBuilder;
import uk.gov.hmcts.reform.unspec.sampledata.CaseDetailsBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.unspec.callback.CallbackType.ABOUT_TO_START;
import static uk.gov.hmcts.reform.unspec.callback.CaseEvent.RESPOND_EXTENSION;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {
    NoOnGoingBusinessProcessAspect.class,
    JacksonAutoConfiguration.class,
    CaseDetailsConverter.class
})
class NoOnGoingBusinessProcessAspectTest {

    @Autowired
    NoOnGoingBusinessProcessAspect aspect;
    @MockBean
    ProceedingJoinPoint proceedingJoinPoint;

    @Test
    @SneakyThrows
    void shouldProceedToMethodInvocation_whenNoOngoingBusinessProcess() {
        AboutToStartOrSubmitCallbackResponse response = AboutToStartOrSubmitCallbackResponse.builder().build();
        when(proceedingJoinPoint.proceed()).thenReturn(response);

        CallbackParams callbackParams = CallbackParamsBuilder.builder()
            .type(ABOUT_TO_START)
            .request(CallbackRequest.builder()
                         .eventId(RESPOND_EXTENSION.name())
                         .caseDetails(CaseDetailsBuilder.builder().atStateExtensionRequested().build())
                         .build())
            .build();
        Object result = aspect.checkOngoingBusinessProcess(proceedingJoinPoint, callbackParams);

        assertThat(result).isEqualTo(response);
        verify(proceedingJoinPoint).proceed();
    }
}
