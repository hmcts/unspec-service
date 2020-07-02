package uk.gov.hmcts.reform.ucmc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.fees.client.FeesClient;
import uk.gov.hmcts.reform.fees.client.model.FeeLookupResponseDto;
import uk.gov.hmcts.reform.payments.client.models.FeeDto;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

@Service
public class FeeService {

    private static final BigDecimal PENCE_PER_POUND = BigDecimal.valueOf(100);

    private final FeesClient feesClient;
    private final String channel;
    private final String event;

    @Autowired
    public FeeService(FeesClient feesClient,
                      @Value("${fees.api.channel:}") String channel,
                      @Value("${fees.api.event:}") String event) {
        this.feesClient = feesClient;
        this.channel = channel;
        this.event = event;
    }

    public BigInteger getFeeAmountByClaimValue(BigDecimal claimValue) {
        FeeLookupResponseDto feeLookupResponseDto = lookupFee(claimValue);

        return getFeeAmountInPence(feeLookupResponseDto);
    }

    public FeeDto getFeeDataByClaimValue(BigDecimal claimValue) {
        FeeLookupResponseDto feeLookupResponseDto = lookupFee(claimValue);

        return buildFeeDto(feeLookupResponseDto);
    }

    private FeeLookupResponseDto lookupFee(BigDecimal claimValue) {
        var claimValuePounds = convertToPounds(claimValue);

        return feesClient.lookupFee(channel, event, claimValuePounds);
    }

    private BigDecimal convertToPounds(BigDecimal value) {
        return value.divide(PENCE_PER_POUND, RoundingMode.UNNECESSARY);
    }

    private BigInteger getFeeAmountInPence(FeeLookupResponseDto feeLookupResponseDto) {
        var feeAmountPounds = feeLookupResponseDto.getFeeAmount();

        return feeAmountPounds.multiply(PENCE_PER_POUND).toBigInteger();
    }

    private FeeDto buildFeeDto(FeeLookupResponseDto feeLookupResponseDto) {
        return FeeDto.builder()
            .calculatedAmount(feeLookupResponseDto.getFeeAmount())
            .code(feeLookupResponseDto.getCode())
            .version(feeLookupResponseDto.getVersion().toString())
            .build();
    }
}
