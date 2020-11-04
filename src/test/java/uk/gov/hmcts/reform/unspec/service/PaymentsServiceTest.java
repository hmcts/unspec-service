package uk.gov.hmcts.reform.unspec.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.payments.client.PaymentsClient;
import uk.gov.hmcts.reform.payments.client.models.FeeDto;
import uk.gov.hmcts.reform.payments.client.models.PaymentDto;
import uk.gov.hmcts.reform.payments.client.request.CreditAccountPaymentRequest;
import uk.gov.hmcts.reform.unspec.config.PaymentsConfiguration;
import uk.gov.hmcts.reform.unspec.model.CaseData;
import uk.gov.hmcts.reform.unspec.model.ClaimFee;
import uk.gov.hmcts.reform.unspec.request.RequestData;
import uk.gov.hmcts.reform.unspec.utils.MonetaryConversions;

import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static uk.gov.hmcts.reform.unspec.enums.PbaNumber.PBA0077597;
import static uk.gov.hmcts.reform.unspec.utils.MonetaryConversions.penniesToPounds;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {JacksonAutoConfiguration.class})
class PaymentsServiceTest {

    private static final String SERVICE = "service";
    private static final String SITE_ID = "site_id";
    private static final String AUTH_TOKEN = "Bearer token";
    private static final ClaimFee CLAIM_FEE = ClaimFee.builder()
        .version("1")
        .code("my code")
        .feeAmount(BigInteger.valueOf(1000))
        .description("my description")
        .build();
    private static final FeeDto[] FEE_DATA = new FeeDto[]{
        FeeDto.builder()
            .ccdCaseNumber("12345")
            .calculatedAmount(penniesToPounds(CLAIM_FEE.getFeeAmount()))
            .code(CLAIM_FEE.getCode())
            .version(String.valueOf(CLAIM_FEE.getVersion()))
            .build()
    };
    private static final PaymentDto PAYMENT_DTO = PaymentDto.builder().reference("RC-1234-1234-1234-1234").build();

    @Mock
    private PaymentsClient paymentsClient;

    @Mock
    private RequestData requestData;

    @Mock
    private PaymentsConfiguration paymentsConfiguration;

    @InjectMocks
    private PaymentsService paymentsService;

    @BeforeEach
    void setUp() {
        given(paymentsClient.createCreditAccountPayment(any(), any())).willReturn(PAYMENT_DTO);
        given(requestData.authorisation()).willReturn(AUTH_TOKEN);
        given(paymentsConfiguration.getService()).willReturn(SERVICE);
        given(paymentsConfiguration.getSiteId()).willReturn(SITE_ID);

        paymentsService = new PaymentsService(
            paymentsClient,
            requestData,
            paymentsConfiguration
        );
    }

    @Test
    void shouldCreateCreditAccountPayment_whenValidCaseDetails() {
        CaseData caseData = CaseData.builder()
            .legacyCaseReference("000LR001")
            .ccdCaseReference(12345L)
            .pbaNumber(PBA0077597)
            .claimFee(CLAIM_FEE)
            .build();
        var expectedCreditAccountPaymentRequest = CreditAccountPaymentRequest.builder()
            .accountNumber("PBA0077597")
            .amount(MonetaryConversions.penniesToPounds(CLAIM_FEE.getFeeAmount()))
            .caseReference("000LR001")
            .ccdCaseNumber("12345")
            .customerReference("Test Customer Reference")
            .description("Claim issue payment")
            .organisationName("Test Organisation Name")
            .service(SERVICE)
            .siteId(SITE_ID)
            .fees(FEE_DATA)
            .build();

        PaymentDto paymentResponse = paymentsService.createCreditAccountPayment(caseData);

        verify(paymentsClient).createCreditAccountPayment(AUTH_TOKEN, expectedCreditAccountPaymentRequest);
        assertThat(paymentResponse).isEqualTo(PAYMENT_DTO);
    }
}
