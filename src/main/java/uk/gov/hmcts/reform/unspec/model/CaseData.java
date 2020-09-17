package uk.gov.hmcts.reform.unspec.model;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.reform.unspec.enums.ClaimType;
import uk.gov.hmcts.reform.unspec.enums.ServedDocuments;
import uk.gov.hmcts.reform.unspec.model.common.Element;
import uk.gov.hmcts.reform.unspec.model.documents.CaseDocument;
import uk.gov.hmcts.reform.unspec.validation.groups.ConfirmServiceDateGroup;
import uk.gov.hmcts.reform.unspec.validation.interfaces.HasServiceDateTheSameAsOrAfterIssueDate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.PastOrPresent;

@Data
@Builder(toBuilder = true)
@HasServiceDateTheSameAsOrAfterIssueDate(groups = ConfirmServiceDateGroup.class)
public class CaseData {

    private final Long ccdCaseReference;
    private final SolicitorReferences solicitorReferences;
    private final CourtLocation courtLocation;
    private final Party applicant1;
    private final Party applicant2;
    private final Party respondent1;
    private final Party respondent2;
    private final ClaimValue claimValue;
    private final ClaimType claimType;
    private final StatementOfTruth applicantSolicitor1ClaimStatementOfTruth;
    private final StatementOfTruth applicant1ServiceStatementOfTruthToRespondentSolicitor1;
    private final List<Element<CaseDocument>> systemGeneratedCaseDocuments;
    private final ServiceMethod serviceMethodToRespondentSolicitor1;

    @PastOrPresent(message = "The date must not be in the future", groups = ConfirmServiceDateGroup.class)
    private final LocalDate serviceDateToRespondentSolicitor1;

    @PastOrPresent(message = "The date must not be in the future", groups = ConfirmServiceDateGroup.class)
    private final LocalDateTime serviceDateTimeToRespondentSolicitor1;

    private final LocalDateTime claimSubmittedDateTime;
    private final LocalDate claimIssuedDate;
    private final LocalDate deemedServiceDateToRespondentSolicitor1;
    private final LocalDateTime respondentSolicitor1ResponseDeadline;
    private final List<ServedDocuments> servedDocuments;
    private final String legacyCaseReference;
    private final ServiceLocation serviceLocationToRespondentSolicitor1;
    private final ServedDocumentFiles servedDocumentFiles;
    private final String servedDocumentsOther;

    @Valid
    private final CloseClaim withdrawClaim;

    @Valid
    private final CloseClaim discontinueClaim;

    private final BusinessProcess businessProcess;
}
