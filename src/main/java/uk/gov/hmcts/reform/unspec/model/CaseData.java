package uk.gov.hmcts.reform.unspec.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.reform.unspec.enums.ServedDocuments;
import uk.gov.hmcts.reform.unspec.enums.ServiceMethod;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CaseData {

    private final SolicitorReferences solicitorReferences;
    private final CourtLocation courtLocation;
    private final Party claimant;
    private final ClaimValue claimValue;
    private final List<ServedDocuments> servedDocuments;
    private final String servedDocumentsOther;
    private final ServedDocumentFiles servedDocumentFiles;
    private final ServiceMethod serviceMethod;
    private final ServiceLocation serviceLocation;
    private final LocalDate serviceDate;
    private final StatementOfTruth claimStatementOfTruth;
    private final StatementOfTruth serviceStatementOfTruth;
    private final LocalDate deemedDateOfService;
    private final LocalDateTime responseDeadline;
}
