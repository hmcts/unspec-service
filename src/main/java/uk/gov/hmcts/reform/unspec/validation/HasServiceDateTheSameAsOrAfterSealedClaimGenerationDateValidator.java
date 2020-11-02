package uk.gov.hmcts.reform.unspec.validation;

import uk.gov.hmcts.reform.unspec.model.CaseData;
import uk.gov.hmcts.reform.unspec.model.documents.CaseDocument;
import uk.gov.hmcts.reform.unspec.validation.interfaces.HasServiceDateTheSameAsOrAfterSealedClaimGenerationDate;

import java.time.LocalDate;
import java.util.Optional;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static uk.gov.hmcts.reform.unspec.helpers.CaseDocumentHelper.findDocument;
import static uk.gov.hmcts.reform.unspec.model.documents.DocumentType.SEALED_CLAIM;

public class HasServiceDateTheSameAsOrAfterSealedClaimGenerationDateValidator implements
    ConstraintValidator<HasServiceDateTheSameAsOrAfterSealedClaimGenerationDate, CaseData> {

    @Override
    public boolean isValid(CaseData caseData, ConstraintValidatorContext context) {
        LocalDate serviceDate;
        if (caseData.getServiceMethodToRespondentSolicitor1().requiresDateEntry()) {
            serviceDate = caseData.getServiceDateToRespondentSolicitor1();
        } else {
            serviceDate = caseData.getServiceDateTimeToRespondentSolicitor1().toLocalDate();
        }
        Optional<CaseDocument> caseDocument = findDocument(caseData.getSystemGeneratedCaseDocuments(), SEALED_CLAIM);
        LocalDate sealedClaimCreationDate = caseDocument.orElseThrow(IllegalStateException::new)
            .getCreatedDatetime().toLocalDate();
        return serviceDate.equals(sealedClaimCreationDate) || serviceDate.isAfter(sealedClaimCreationDate);
    }
}
