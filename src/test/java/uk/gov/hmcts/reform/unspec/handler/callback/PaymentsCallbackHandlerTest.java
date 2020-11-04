package uk.gov.hmcts.reform.unspec.handler.callback;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.reform.payments.client.models.FeeDto;
import uk.gov.hmcts.reform.payments.client.models.PaymentDto;
import uk.gov.hmcts.reform.unspec.callback.CallbackParams;
import uk.gov.hmcts.reform.unspec.config.PaymentsConfiguration;
import uk.gov.hmcts.reform.unspec.helpers.CaseDetailsConverter;
import uk.gov.hmcts.reform.unspec.model.CaseData;
import uk.gov.hmcts.reform.unspec.sampledata.CaseDataBuilder;
import uk.gov.hmcts.reform.unspec.service.FeesService;
import uk.gov.hmcts.reform.unspec.service.PaymentsService;

import java.math.BigDecimal;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.unspec.callback.CallbackType.ABOUT_TO_SUBMIT;

@SpringBootTest(classes = {PaymentsCallbackHandler.class, JacksonAutoConfiguration.class, CaseDetailsConverter.class})
public class PaymentsCallbackHandlerTest extends BaseCallbackHandlerTest {

    private static final String SUCCESSFUL_PAYMENT_REFERENCE = "RC-1234-1234-1234-1234";

    @MockBean
    private PaymentsConfiguration paymentsConfiguration;
    @MockBean
    private PaymentsService paymentsService;
    @MockBean
    private FeesService feesService;

    @Autowired
    private PaymentsCallbackHandler handler;

    private static final FeeDto FEE_DATA = FeeDto.builder()
        .version("1")
        .code("my code")
        .calculatedAmount(BigDecimal.TEN)
        .description("my description")
        .build();

    @Test
    void shouldMakePbaPayment_whenInvoked() {
        given(feesService.getFeeDataByClaimValue(any())).willReturn(FEE_DATA);
        when(paymentsConfiguration.isEnabled()).thenReturn(true);
        when(paymentsService.createCreditAccountPayment(any(), eq(FEE_DATA)))
            .thenReturn(PaymentDto.builder().reference(SUCCESSFUL_PAYMENT_REFERENCE).build());
        CaseData caseData = CaseDataBuilder.builder().atStateClaimCreated().build();
        CallbackParams params = callbackParamsOf(new HashMap<>(), ABOUT_TO_SUBMIT)
            .toBuilder().caseData(caseData).build();

        var response = (AboutToStartOrSubmitCallbackResponse) handler.handle(params);

        verify(paymentsService).createCreditAccountPayment(caseData, FEE_DATA);
        assertThat(response.getData())
            .extracting("paymentReference")
            .isEqualTo(SUCCESSFUL_PAYMENT_REFERENCE);

        assertThat(response.getData())
            .extracting("claimFee")
            .extracting("code", "description", "feeAmount", "version")
            .containsExactlyInAnyOrder("my code", "my description", "1000", "1");

    }
}
