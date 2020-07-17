package uk.gov.hmcts.reform.unspec.validation;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;

import java.util.List;

import static com.google.common.collect.ImmutableMap.of;
import static java.time.LocalDate.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {RequestExtensionValidator.class, JacksonAutoConfiguration.class})
class RequestExtensionValidatorTest {

    @Autowired
    RequestExtensionValidator validator;

    @Nested
    class ValidateProposedDeadLine {
        @Test
        void shouldReturnNoErrors_whenValidProposedDeadline() {

            CaseDetails caseDetails = CaseDetails.builder()
                .data(of("extensionProposedDeadline", now().plusDays(14),
                         "responseDeadline", now().plusDays(7).atTime(16, 0)
                ))
                .build();

            List<String> errors = validator.validateProposedDeadline(caseDetails);

            assertThat(errors.isEmpty()).isTrue();
        }

        @Test
        void shouldReturnErrors_whenProposedDeadlineIsAfter28DaysFromResponseDeadline() {

            CaseDetails caseDetails = CaseDetails.builder()
                .data(of("extensionProposedDeadline", now().plusDays(29),
                         "responseDeadline", now().atTime(16, 0)
                ))
                .build();

            List<String> errors = validator.validateProposedDeadline(caseDetails);

            assertAll(
                () -> assertThat(errors.isEmpty()).isFalse(),
                () -> assertEquals(1, errors.size()),
                () -> assertEquals(
                    "The proposed deadline can't be later than 28 days after the current deadline.",
                    errors.get(0)
                )
            );
        }

        @Test
        void shouldReturnError_whenProposedDeadlineInIsNotInFuture() {
            CaseDetails caseDetails = CaseDetails.builder()
                .data(of("extensionProposedDeadline", now(),
                         "responseDeadline", now().atTime(16, 0)
                ))
                .build();

            List<String> errors = validator.validateProposedDeadline(caseDetails);

            assertAll(
                () -> assertThat(errors.isEmpty()).isFalse(),
                () -> assertEquals(1, errors.size()),
                () -> assertEquals(
                    "The proposed deadline must be a future date.",
                    errors.get(0)
                )
            );
        }
    }

    @Nested
    class ExtensionAlreadyRequested {
        @Test
        void shouldReturnErrors_whenExtensionAlreadyRequested() {

            CaseDetails caseDetails = CaseDetails.builder()
                .data(of("extensionProposedDeadline", now().plusDays(14),
                         "responseDeadline", now().plusDays(7).atTime(16, 0)
                ))
                .build();

            List<String> errors = validator.validateAlreadyRequested(caseDetails);

            assertAll(
                () -> assertThat(errors.isEmpty()).isFalse(),
                () -> assertEquals(1, errors.size()),
                () -> assertEquals(
                    "A request for extension can only be requested once.",
                    errors.get(0)
                )
            );
        }

        @Test
        void shouldReturnNoError_whenExtensionRequestedFirstTime() {

            List<String> errors = validator.validateAlreadyRequested(
                CaseDetails.builder()
                    .data(of("responseDeadline", now().atTime(16, 0)))
                    .build()
            );

            assertThat(errors.isEmpty()).isTrue();
        }
    }
}
