package uk.gov.hmcts.reform.ucmc.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.reform.ucmc.model.common.Element;
import uk.gov.hmcts.reform.ucmc.model.documents.CaseDocument;

import java.util.List;

@Data
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CaseData {
    private Long id;
    private final SolicitorReferences solicitorReferences;
    private final CourtLocation courtLocation;
    private final Applicant claimant;
    private final Defendant respondent;
    private final ClaimValue claimValue;
    private final StatementOfTruth statementOfTruth;
    private final List<Element<CaseDocument>> systemGeneratedCaseDocuments;


}
