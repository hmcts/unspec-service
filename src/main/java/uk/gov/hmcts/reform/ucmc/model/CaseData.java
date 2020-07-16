package uk.gov.hmcts.reform.ucmc.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.reform.ucmc.enums.ServedDocuments;
import uk.gov.hmcts.reform.ucmc.enums.ServiceMethod;
import uk.gov.hmcts.reform.ucmc.model.common.Element;
import uk.gov.hmcts.reform.ucmc.model.documents.CaseDocument;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CaseData {
    private Long id;
    private final SolicitorReferences solicitorReferences;
    private final CourtLocation courtLocation;
    private final Party claimant;
    private final Party respondent;
    private final ClaimValue claimValue;
    private final StatementOfTruth claimStatementOfTruth;
    private final List<Element<CaseDocument>> systemGeneratedCaseDocuments;
    private final ServiceMethod serviceMethod;
    private final LocalDate serviceDate;
    private final LocalDate deemedDateOfService;
    private final LocalDateTime responseDeadline;
    private final List<ServedDocuments> servedDocuments;
    private final String referenceNumber;
}
