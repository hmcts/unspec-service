package uk.gov.hmcts.reform.ucmc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.reform.ucmc.model.documents.CaseDocument;

import javax.lang.model.element.Element;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class CaseData {
    private List<Element<CaseDocument>> systemGeneratedCaseDocuments;

}
