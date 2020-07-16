package uk.gov.hmcts.reform.ucmc.validation;

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

    @Test
    void shouldReturnNoErrorsWhenValidProposedDeadline() {

        CaseDetails caseDetails = CaseDetails.builder()
            .data(of("extensionProposedDeadline", now().plusDays(14),
                     "responseDeadline", now().plusDays(7).atTime(16, 0)
            ))
            .build();

        List<String> errors = validator.validateProposedDeadline(caseDetails);

        assertThat(errors.isEmpty()).isTrue();
    }

    @Test
    void shouldReturnErrorsWhenProposedDeadlineIsAfter28DaysFromResponseDeadline() {

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
    void shouldReturnErrorWhenProposedDeadlineInIsNotInFuture() {
        CaseDetails caseDetails = CaseDetails.builder()
            .data(of("extensionProposedDeadline", now(),
                     "responseDeadline", now().atTime(16, 0)
            ))
            .build();

        List<String> errors = validator.validateProposedDeadline(caseDetails);

        assertAll(
            () -> assertThat(errors.isEmpty()).isFalse(),
            () -> assertEquals(1, errors.size()),
            () -> assertEquals("The proposed deadline must be a future date.", errors.get(0))
        );
    }

    @Test
    void shouldReturnErrorsWhenExtensionAlreadyRequested() {

        CaseDetails caseDetails = CaseDetails.builder()
            .data(of("extensionProposedDeadline", now().plusDays(14),
                     "responseDeadline", now().plusDays(7).atTime(16, 0)
            ))
            .build();

        List<String> errors = validator.validateAlreadyRequested(caseDetails);

        assertAll(
            () -> assertThat(errors.isEmpty()).isFalse(),
            () -> assertEquals(1, errors.size()),
            () -> assertEquals("A request for extension can only be requested once.", errors.get(0))
        );
    }

    @Test
    void shouldReturnNoErrorsWhenExtensionRequestedFirstTime() {

        List<String> errors = validator.validateAlreadyRequested(
            CaseDetails.builder()
                .data(of("responseDeadline", now().atTime(16, 0)))
                .build()
        );

        assertThat(errors.isEmpty()).isTrue();
    }
}
