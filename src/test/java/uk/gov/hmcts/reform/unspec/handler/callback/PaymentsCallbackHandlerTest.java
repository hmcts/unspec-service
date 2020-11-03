package uk.gov.hmcts.reform.unspec.handler.callback;

import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.reform.payments.client.models.PaymentDto;
import uk.gov.hmcts.reform.unspec.callback.CallbackParams;
import uk.gov.hmcts.reform.unspec.config.PaymentsConfiguration;
import uk.gov.hmcts.reform.unspec.helpers.CaseDetailsConverter;
import uk.gov.hmcts.reform.unspec.model.CaseData;
import uk.gov.hmcts.reform.unspec.sampledata.CaseDataBuilder;
import uk.gov.hmcts.reform.unspec.service.PaymentsService;

import java.util.HashMap;
import java.util.Map;

import static feign.Request.HttpMethod.GET;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.unspec.callback.CallbackType.ABOUT_TO_SUBMIT;

@SpringBootTest(classes = {
    PaymentsCallbackHandler.class,
    JacksonAutoConfiguration.class,
    CaseDetailsConverter.class
})
public class PaymentsCallbackHandlerTest extends BaseCallbackHandlerTest {

    private static final String SUCCESSFUL_PAYMENT_REFERENCE = "RC-1234-1234-1234-1234";
    private static final String PAYMENT_ERROR_RESPONSE = "Payment API error";

    @MockBean
    private PaymentsConfiguration paymentsConfiguration;

    @MockBean
    private PaymentsService paymentsService;

    @Autowired
    private PaymentsCallbackHandler handler;

    private CaseData caseData;
    private CallbackParams params;

    @BeforeEach
    public void setup() {
        when(paymentsConfiguration.isEnabled()).thenReturn(true);
        caseData = CaseDataBuilder.builder().atStatePendingCreated().build();
        params = callbackParamsOf(new HashMap<>(), ABOUT_TO_SUBMIT)
            .toBuilder().caseData(caseData).build();
    }

    @Test
    void shouldMakePbaPayment_whenInvoked() {
        when(paymentsService.createCreditAccountPayment(any()))
            .thenReturn(PaymentDto.builder().reference(SUCCESSFUL_PAYMENT_REFERENCE).build());

        var response = (AboutToStartOrSubmitCallbackResponse) handler.handle(params);

        verify(paymentsService).createCreditAccountPayment(caseData);
        assertThat(response.getData()).extracting("paymentReference").isEqualTo(SUCCESSFUL_PAYMENT_REFERENCE);
    }

    @ParameterizedTest
    @ValueSource(ints = {403, 404, 422})
    void shouldUpdateFailureReason_whenSpecificExceptionThrown(int status) {
        doThrow(buildFeignException(status)).when(paymentsService).createCreditAccountPayment(any());

        var response = (AboutToStartOrSubmitCallbackResponse) handler.handle(params);

        verify(paymentsService).createCreditAccountPayment(caseData);
        assertThat(response.getData()).extracting("paymentReference").isNull();
        assertThat(response.getData()).extracting("paymentFailureReason").isEqualTo(PAYMENT_ERROR_RESPONSE);
        assertThat(response.getErrors()).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(ints = {400, 504})
    void shouldAddError_whenSpecificExceptionThrown(int status) {
        doThrow(buildFeignException(status)).when(paymentsService).createCreditAccountPayment(any());

        var response = (AboutToStartOrSubmitCallbackResponse) handler.handle(params);

        verify(paymentsService).createCreditAccountPayment(caseData);
        assertThat(response.getData()).extracting("paymentReference").isNull();
        assertThat(response.getData()).extracting("paymentFailureReason").isNull();
        assertThat(response.getErrors()).containsOnly("Technical error occurred");
    }

    private FeignException buildFeignException(int status) {
        return new FeignException.FeignClientException(
            status,
            "exception message",
            Request.create(GET, "", Map.of(), new byte[]{}, UTF_8, null),
            PAYMENT_ERROR_RESPONSE.getBytes(UTF_8)
        );
    }
}
