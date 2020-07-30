package uk.gov.hmcts.reform.unspec.service.bankholidays;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

class NonWorkingDaysCollectionTest {

    private NonWorkingDaysCollection collection;

    @Test
    void shouldReturnTrue_whenMatchingNonWorkingDays() {
        collection = new NonWorkingDaysCollection("/non-working-days/nwd-valid.dat");
        assertThat(collection.contains(LocalDate.of(2020, Month.DECEMBER, 2))).isTrue();
    }

    @Test
    void shouldReturnFalse_whenNoNonWorkingDays() {
        collection = new NonWorkingDaysCollection("/non-working-days/nwd-empty-file.dat");
        assertThat(collection.contains(LocalDate.now())).isFalse();
    }

    @Test
    void shouldReturnFalse_whenNonMatchingNonWorkingDays() {
        collection = new NonWorkingDaysCollection("/non-working-days/nwd-valid.dat");
        assertThat(collection.contains(LocalDate.of(2020, Month.DECEMBER, 3))).isFalse();
    }

    @Test
    void shouldReturnFalse_whenIncoherentNonWorkingDays() {
        collection = new NonWorkingDaysCollection("/non-working-days/nwd-invalid.dat");
        assertThat(collection.contains(LocalDate.now())).isFalse();
    }
}
