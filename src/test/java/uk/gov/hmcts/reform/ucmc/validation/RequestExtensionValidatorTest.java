package uk.gov.hmcts.reform.ucmc.validation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.hmcts.reform.ccd.client.model.CallbackRequest;
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
    void shouldReturnNoErrorsWhenValidCallbackRequest() {
        CallbackRequest callbackRequest = CallbackRequest.builder()
            .caseDetails(CaseDetails.builder()
                             .data(of("extensionProposedDeadline", now().plusDays(14)))
                             .build())
            .build();

        List<String> errors = validator.validate(callbackRequest);

        assertThat(errors.isEmpty()).isTrue();
    }

    @Test
    void shouldReturnErrorWhenProposedDeadlineInPast() {
        CallbackRequest callbackRequest = CallbackRequest.builder()
            .caseDetails(CaseDetails.builder()
                             .data(of("extensionProposedDeadline", now().minusDays(1)))
                             .build())
            .build();

        List<String> errors = validator.validate(callbackRequest);

        assertAll(
            () -> assertThat(errors.isEmpty()).isFalse(),
            () -> assertEquals(1, errors.size()),
            () -> assertEquals("The proposed deadline can't be in the past.", errors.get(0))
        );
    }
}
