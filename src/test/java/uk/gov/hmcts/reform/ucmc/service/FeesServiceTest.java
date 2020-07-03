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

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class FeesServiceTest {
    private static final String CHANNEL = "channel";
    private static final String EVENT = "event";
    private static final BigDecimal FEE_AMOUNT_POUNDS = new BigDecimal("1.00");

    @Mock
    private FeesClient feesClient;

    @Mock
    private FeesConfiguration feesConfiguration;

    @InjectMocks
    private FeesService feesService;

    @BeforeEach
    void setup() {
        given(feesClient.lookupFee(any(), any(), any()))
            .willReturn(FeeLookupResponseDto.builder()
                            .feeAmount(FEE_AMOUNT_POUNDS)
                            .code("testcode")
                            .version(1)
                            .build());
        given(feesConfiguration.getChannel()).willReturn(CHANNEL);
        given(feesConfiguration.getEvent()).willReturn(EVENT);
    }

    @Test
    public void shouldReturnFeeAmountForClaimValue() {
        var claimValue = BigDecimal.valueOf(500);

        BigInteger feeAmount = feesService.getFeeAmountByClaimValue(claimValue);

        verify(feesClient).lookupFee(CHANNEL, EVENT, new BigDecimal("5.00"));
        assertThat(feeAmount).isEqualTo(BigInteger.valueOf(100));
    }

    @Test
    public void shouldReturnFeeDataForClaimValue() {
        var claimValue = BigDecimal.valueOf(500);
        FeeDto expectedFeeDto = FeeDto.builder()
            .calculatedAmount(FEE_AMOUNT_POUNDS)
            .code("testcode")
            .version("1")
            .build();

        FeeDto feeDto = feesService.getFeeDataByClaimValue(claimValue);

        verify(feesClient).lookupFee(CHANNEL, EVENT, new BigDecimal("5.00"));
        assertThat(feeDto).isEqualTo(expectedFeeDto);
    }
}
