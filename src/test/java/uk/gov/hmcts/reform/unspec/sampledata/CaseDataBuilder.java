package uk.gov.hmcts.reform.unspec.sampledata;

import uk.gov.hmcts.reform.unspec.enums.ClaimType;
import uk.gov.hmcts.reform.unspec.enums.ServedDocuments;
import uk.gov.hmcts.reform.unspec.model.CaseData;
import uk.gov.hmcts.reform.unspec.model.ClaimValue;
import uk.gov.hmcts.reform.unspec.model.CourtLocation;
import uk.gov.hmcts.reform.unspec.model.Party;
import uk.gov.hmcts.reform.unspec.model.ServedDocumentFiles;
import uk.gov.hmcts.reform.unspec.model.ServiceLocation;
import uk.gov.hmcts.reform.unspec.model.ServiceMethod;
import uk.gov.hmcts.reform.unspec.model.SolicitorReferences;
import uk.gov.hmcts.reform.unspec.model.StatementOfTruth;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static uk.gov.hmcts.reform.unspec.enums.ServedDocuments.CLAIM_FORM;
import static uk.gov.hmcts.reform.unspec.enums.ServedDocuments.OTHER;
import static uk.gov.hmcts.reform.unspec.enums.ServedDocuments.PARTICULARS_OF_CLAIM;
import static uk.gov.hmcts.reform.unspec.enums.ServiceLocationType.BUSINESS;

public class CaseDataBuilder {

    // Create Claim
    private SolicitorReferences solicitorReferences;
    private CourtLocation courtLocation;
    private Party applicant1;
    private Party respondent1;
    private ClaimValue claimValue;
    private ClaimType claimType;
    private StatementOfTruth applicantSolicitor1ClaimStatementOfTruth;
    private LocalDateTime claimSubmittedDateTime;
    private LocalDate claimIssuedDate;
    private String legacyCaseReference;
    private LocalDateTime confirmationOfServiceDeadline;
    // Confirm Service
    private LocalDate deemedServiceDateToRespondentSolicitor1;
    private LocalDateTime respondentSolicitor1ResponseDeadline;
    private ServiceMethod serviceMethodToRespondentSolicitor1;
    private ServiceLocation serviceLocationToRespondentSolicitor1;
    private List<ServedDocuments> servedDocuments;
    private String servedDocumentsOther;
    private ServedDocumentFiles servedDocumentFiles;
    private LocalDate serviceDateToRespondentSolicitor1;
    private LocalDateTime serviceDateTimeToRespondentSolicitor1;
    private StatementOfTruth applicant1ServiceStatementOfTruthToRespondentSolicitor1;

    public CaseDataBuilder atStateClaimDraft() {
        solicitorReferences = SolicitorReferences.builder()
            .applicantSolicitor1Reference("12345")
            .respondentSolicitor1Reference("6789")
            .build();
        courtLocation = CourtLocation.builder()
            .applicantPreferredCourt("The court location")
            .build();
        claimValue = ClaimValue.builder()
            .lowerValue(BigDecimal.valueOf(10000))
            .higherValue(BigDecimal.valueOf(100000))
            .build();
        claimType = ClaimType.PERSONAL_INJURY_WORK;
        applicant1 = PartyBuilder.builder().individual().build();
        respondent1 = PartyBuilder.builder().soleTrader().build();
        applicantSolicitor1ClaimStatementOfTruth = StatementOfTruthBuilder.builder().build();

        return this;
    }

    public CaseDataBuilder atStateClaimCreated() {
        atStateClaimDraft();
        claimSubmittedDateTime = LocalDateTime.now();
        claimIssuedDate = LocalDate.now();
        confirmationOfServiceDeadline = claimIssuedDate.plusMonths(4).atTime(23, 59, 59);
        legacyCaseReference = "000LR001";

        return this;
    }

    public CaseDataBuilder atStateServiceConfirmed() {
        atStateClaimCreated();

        deemedServiceDateToRespondentSolicitor1 = LocalDate.now();
        respondentSolicitor1ResponseDeadline = LocalDate.now().plusDays(14).atTime(23, 59, 59);
        serviceMethodToRespondentSolicitor1 = ServiceMethodBuilder.builder().email().build();
        serviceLocationToRespondentSolicitor1 = ServiceLocation.builder().location(BUSINESS).build();
        serviceDateTimeToRespondentSolicitor1 = LocalDateTime.now();
        servedDocuments = List.of(CLAIM_FORM, PARTICULARS_OF_CLAIM, OTHER);
        servedDocumentsOther = "My other documents";
        applicant1ServiceStatementOfTruthToRespondentSolicitor1 = StatementOfTruthBuilder.builder().build();
        return this;
    }

    public CaseDataBuilder atStateRespondedToClaim() {
        atStateServiceConfirmed();

        //TODO: add additional fields for this state here
        return this;
    }

    public CaseDataBuilder atStateFullDefence() {
        atStateRespondedToClaim();

        //TODO: add additional fields for this state here
        return this;
    }

    public CaseDataBuilder atStateServiceAcknowledge() {
        atStateServiceConfirmed();

        //TODO: add additional fields for this state here
        return this;
    }

    public CaseDataBuilder atStateExtensionRequested() {
        atStateServiceAcknowledge();

        //TODO: add additional fields for this state here
        return this;
    }

    public CaseDataBuilder atStateExtensionResponded() {
        atStateExtensionRequested();

        //TODO: add additional fields for this state here
        return this;
    }

    public static CaseDataBuilder builder() {
        return new CaseDataBuilder();
    }

    public CaseData build() {
        return CaseData.builder()
            // Create Claim
            .claimSubmittedDateTime(claimSubmittedDateTime)
            .claimIssuedDate(claimIssuedDate)
            .legacyCaseReference(legacyCaseReference)
            .confirmationOfServiceDeadline(confirmationOfServiceDeadline)
            .solicitorReferences(solicitorReferences)
            .courtLocation(courtLocation)
            .claimValue(claimValue)
            .claimType(claimType)
            .applicant1(applicant1)
            .respondent1(respondent1)
            .applicantSolicitor1ClaimStatementOfTruth(applicantSolicitor1ClaimStatementOfTruth)
            // Confirm Service
            .deemedServiceDateToRespondentSolicitor1(deemedServiceDateToRespondentSolicitor1)
            .respondentSolicitor1ResponseDeadline(respondentSolicitor1ResponseDeadline)
            .serviceMethodToRespondentSolicitor1(serviceMethodToRespondentSolicitor1)
            .serviceLocationToRespondentSolicitor1(serviceLocationToRespondentSolicitor1)
            .servedDocuments(servedDocuments)
            .servedDocumentsOther(servedDocumentsOther)
            .servedDocumentFiles(servedDocumentFiles)
            .serviceDateToRespondentSolicitor1(serviceDateToRespondentSolicitor1)
            .serviceDateTimeToRespondentSolicitor1(serviceDateTimeToRespondentSolicitor1)
            .applicant1ServiceStatementOfTruthToRespondentSolicitor1(
                applicant1ServiceStatementOfTruthToRespondentSolicitor1
            )
            //
            .build();
    }
}
