package uk.gov.hmcts.reform.unspec.helpers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.hmcts.reform.unspec.helpers.ExponentialRetryTimeoutHelper.calculateExponentialRetryTimeout;

class ExponentialRetryTimeoutHelperTest {

    public static final int START_VALUE = 500;

    @Test
    void shouldStartWithInitialValueForRetryTime_whenAllRetriesLeft() {
        assertEquals(calculateExponentialRetryTimeout(START_VALUE, 3, 3), START_VALUE);
    }

    @Test
    void shouldRiseExponentially_whenRemainingRetriesLessThanTotalRetries() {
        assertEquals(calculateExponentialRetryTimeout(START_VALUE, 3, 1), 2000);
    }

    @Test
    void shouldReturn0_whenRemainingRetriesIsLessThan0() {
        assertEquals(calculateExponentialRetryTimeout(START_VALUE, 3, -1), 0);
    }

    @Test
    void shouldReturn0_whenRemainingRetriesIsMoreThanTotal() {
        assertEquals(calculateExponentialRetryTimeout(START_VALUE, 3, 4), 0);
    }

    @Test
    void shouldReturn0_whenTotalAndRemainingRetriesAre0() {
        assertEquals(calculateExponentialRetryTimeout(START_VALUE, 0, 0), 0);
    }
}
