package uk.gov.hmcts.reform.unspec.validation;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.unspec.model.UnavailableDate;

import java.time.LocalDate;
import javax.validation.ConstraintValidatorContext;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class UnavailableDateValidatorTest {

    @Mock
    ConstraintValidatorContext constraintValidatorContext;

    @InjectMocks
    UnavailableDateValidator validator;

    @Nested
    class FutureDate {

        @Test
        void shouldBeValidDate_whenLessThanOneYearInTheFuture() {
            UnavailableDate date = UnavailableDate.builder()
                .date(LocalDate.now().plusMonths(4))
                .build();

            assertTrue(validator.isValid(date, constraintValidatorContext));
        }

        @Test
        void shouldBeValidDate_whenIsExactlyThanOneYearInTheFuture() {
            UnavailableDate date = UnavailableDate.builder()
                .date(LocalDate.now().plusYears(1))
                .build();

            assertTrue(validator.isValid(date, constraintValidatorContext));
        }

        @Test
        void shouldBeInvalidDate_whenIsOneDayMoreThanOneYearInTheFuture() {
            UnavailableDate date = UnavailableDate.builder()
                .date(LocalDate.now().plusYears(1).plusDays(1))
                .build();

            assertFalse(validator.isValid(date, constraintValidatorContext));
        }

        @Test
        void shouldBeInvalidDate_whenIsMoreThanOneYearInTheFuture() {
            UnavailableDate date = UnavailableDate.builder()
                .date(LocalDate.now().plusYears(2))
                .build();

            assertFalse(validator.isValid(date, constraintValidatorContext));
        }
    }

    @Nested
    class PastDate {

        @Test
        void shouldBeInvalidDate_whenInThePast() {
            UnavailableDate date = UnavailableDate.builder()
                .date(LocalDate.now().minusYears(1))
                .build();

            assertFalse(validator.isValid(date, constraintValidatorContext));
        }

        @Test
        void shouldBeValidDate_whenToday() {
            UnavailableDate date = UnavailableDate.builder()
                .date(LocalDate.now())
                .build();

            assertTrue(validator.isValid(date, constraintValidatorContext));
        }
    }
}
