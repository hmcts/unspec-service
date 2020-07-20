package uk.gov.hmcts.reform.unspec.model;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.reform.unspec.enums.ClaimType;
import uk.gov.hmcts.reform.unspec.enums.ServedDocuments;
import uk.gov.hmcts.reform.unspec.enums.ServiceMethod;
import uk.gov.hmcts.reform.unspec.model.common.Element;
import uk.gov.hmcts.reform.unspec.model.documents.CaseDocument;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder(toBuilder = true)
public class CaseData {
    private Long ccdCaseReference;
    private SolicitorReferences solicitorReferences;
    private CourtLocation courtLocation;
    private Party claimant;
    private Party respondent;
    private ClaimValue claimValue;
    private ClaimType claimType;
    private StatementOfTruth claimStatementOfTruth;
    private StatementOfTruth serviceStatementOfTruth;
    private List<Element<CaseDocument>> systemGeneratedCaseDocuments;
    private ServiceMethod serviceMethod;
    private LocalDate serviceDate;
    private LocalDate deemedDateOfService;
    private LocalDateTime responseDeadline;
    private List<ServedDocuments> servedDocuments;
    private String legacyCaseReference;
    private ServiceLocation serviceLocation;
    private ServedDocumentFiles servedDocumentFiles;
    private String servedDocumentsOther;
}
