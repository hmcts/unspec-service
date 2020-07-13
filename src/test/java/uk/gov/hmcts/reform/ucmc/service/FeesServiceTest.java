package uk.gov.hmcts.reform.ucmc.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.fees.client.FeesClient;
import uk.gov.hmcts.reform.fees.client.model.FeeLookupResponseDto;
import uk.gov.hmcts.reform.payments.client.models.FeeDto;
import uk.gov.hmcts.reform.ucmc.config.FeesConfiguration;
import uk.gov.hmcts.reform.ucmc.model.ClaimValue;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class FeesServiceTest {
    private static final String CHANNEL = "channel";
    private static final String EVENT = "event";
    private static final BigDecimal TEST_FEE_AMOUNT_POUNDS = new BigDecimal("1.00");
    private static final BigDecimal MAX_FEE_AMOUNT_POUNDS = new BigDecimal("10000.00");
    private static final BigInteger TEST_FEE_PENCE = BigInteger.valueOf(100);
    private static final BigInteger MAX_FEE_PENCE = BigInteger.valueOf(1000000);

    @Mock
    private FeesClient feesClient;

    @Mock
    private FeesConfiguration feesConfiguration;

    @InjectMocks
    private FeesService feesService;

    @BeforeEach
    void setUp() {
        given(feesClient.lookupFee(any(), any(), eq(new BigDecimal("50.00"))))
            .willReturn(FeeLookupResponseDto.builder()
                            .feeAmount(TEST_FEE_AMOUNT_POUNDS)
                            .code("test_fee_code")
                            .version(1)
                            .build());
        given(feesClient.lookupFee(any(), any(), eq(new BigDecimal("200000.01"))))
            .willReturn(FeeLookupResponseDto.builder()
                            .feeAmount(MAX_FEE_AMOUNT_POUNDS)
                            .code("max_fee_code")
                            .version(2)
                            .build());
        given(feesConfiguration.getChannel()).willReturn(CHANNEL);
        given(feesConfiguration.getEvent()).willReturn(EVENT);
    }

    @Test
    public void shouldReturnFeeAmountWhenValidClaimValue() {
        var claimValue = ClaimValue.builder()
            .lowerValue(BigDecimal.valueOf(100))
            .higherValue(BigDecimal.valueOf(5000))
            .build();

        BigInteger feeAmount = feesService.getFeeAmountByClaimValue(claimValue);

        verify(feesClient).lookupFee(CHANNEL, EVENT, new BigDecimal("50.00"));
        assertThat(feeAmount).isEqualTo(TEST_FEE_PENCE);
    }

    @Test
    public void shouldReturnMaxFeeAmountWhenClaimValueHaveNoHigherValue() {
        var claimValue = ClaimValue.builder()
            .lowerValue(BigDecimal.valueOf(100))
            .build();

        BigInteger feeAmount = feesService.getFeeAmountByClaimValue(claimValue);

        verify(feesClient).lookupFee(CHANNEL, EVENT, new BigDecimal("200000.01"));
        assertThat(feeAmount).isEqualTo(MAX_FEE_PENCE);
    }

    @Test
    public void shouldReturnFeeDataWhenValidClaimValue() {
        var claimValue = ClaimValue.builder()
            .lowerValue(BigDecimal.valueOf(100))
            .higherValue(BigDecimal.valueOf(5000))
            .build();

        FeeDto expectedFeeDto = FeeDto.builder()
            .calculatedAmount(TEST_FEE_AMOUNT_POUNDS)
            .code("test_fee_code")
            .version("1")
            .build();

        FeeDto feeDto = feesService.getFeeDataByClaimValue(claimValue);

        verify(feesClient).lookupFee(CHANNEL, EVENT, new BigDecimal("50.00"));
        assertThat(feeDto).isEqualTo(expectedFeeDto);
    }

    @Test
    public void shouldReturnMaxFeeDataWhenClaimValueHaveNoHigherValue() {
        var claimValue = ClaimValue.builder()
            .lowerValue(BigDecimal.valueOf(100))
            .build();

        FeeDto expectedFeeDto = FeeDto.builder()
            .calculatedAmount(MAX_FEE_AMOUNT_POUNDS)
            .code("max_fee_code")
            .version("2")
            .build();

        FeeDto feeDto = feesService.getFeeDataByClaimValue(claimValue);

        verify(feesClient).lookupFee(CHANNEL, EVENT, new BigDecimal("200000.01"));
        assertThat(feeDto).isEqualTo(expectedFeeDto);
    }
}
