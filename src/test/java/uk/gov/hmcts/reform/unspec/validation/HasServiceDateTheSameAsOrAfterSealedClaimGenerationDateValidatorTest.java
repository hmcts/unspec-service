package uk.gov.hmcts.reform.unspec.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.unspec.enums.ServiceMethodType;
import uk.gov.hmcts.reform.unspec.model.CaseData;
import uk.gov.hmcts.reform.unspec.model.ServiceMethod;
import uk.gov.hmcts.reform.unspec.model.common.Element;
import uk.gov.hmcts.reform.unspec.model.documents.CaseDocument;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.ConstraintValidatorContext;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.hmcts.reform.unspec.model.documents.DocumentType.SEALED_CLAIM;

@ExtendWith(MockitoExtension.class)
class HasServiceDateTheSameAsOrAfterSealedClaimGenerationDateValidatorTest {

    public static final LocalDate NOW = LocalDate.now();

    @Mock
    ConstraintValidatorContext constraintValidatorContext;

    @InjectMocks
    private HasServiceDateTheSameAsOrAfterSealedClaimGenerationDateValidator validator;

    @Test
    void shouldReturnFalse_whenServiceDateIsBeforeIssueDate() {
        assertFalse(validator.isValid(buildCaseDataWithServiceDateOf(NOW.minusDays(5)), constraintValidatorContext));
    }

    @Test
    void shouldReturnTrue_whenServiceDateIsOnIssueDate() {
        assertTrue(validator.isValid(buildCaseDataWithServiceDateOf(NOW), constraintValidatorContext));
    }

    @Test
    void shouldReturnTrue_whenServiceDateIsAfterIssueDate() {
        assertTrue(validator.isValid(buildCaseDataWithServiceDateOf(NOW.plusDays(5)), constraintValidatorContext));
    }

    private CaseData buildCaseDataWithServiceDateOf(LocalDate serviceDate) {
        Element<CaseDocument> documents = Element.<CaseDocument>builder()
            .value(CaseDocument.builder().documentType(SEALED_CLAIM).createdDatetime(LocalDateTime.now()).build())
            .build();
        return CaseData.builder()
            .systemGeneratedCaseDocuments(List.of(documents))
            .serviceMethodToRespondentSolicitor1(ServiceMethod.builder().type(ServiceMethodType.POST).build())
            .serviceDateToRespondentSolicitor1(serviceDate)
            .build();
    }
}
