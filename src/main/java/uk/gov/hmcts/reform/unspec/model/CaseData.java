package uk.gov.hmcts.reform.unspec.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.reform.unspec.enums.ServedDocuments;
import uk.gov.hmcts.reform.unspec.enums.ServiceMethod;
import uk.gov.hmcts.reform.unspec.model.common.Element;
import uk.gov.hmcts.reform.unspec.model.documents.CaseDocument;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CaseData {
    private Long id;
    private SolicitorReferences solicitorReferences;
    private CourtLocation courtLocation;
    private Party claimant;
    private Party respondent;
    private ClaimValue claimValue;
    private StatementOfTruth claimStatementOfTruth;
    private StatementOfTruth serviceStatementOfTruth;
    private List<Element<CaseDocument>> systemGeneratedCaseDocuments;
    private ServiceMethod serviceMethod;
    private LocalDate serviceDate;
    private LocalDate deemedDateOfService;
    private LocalDateTime responseDeadline;
    private List<ServedDocuments> servedDocuments;
    private String referenceNumber;
    private ServiceLocation serviceLocation;
    private ServedDocumentFiles servedDocumentFiles;
    private String servedDocumentsOther;
}
