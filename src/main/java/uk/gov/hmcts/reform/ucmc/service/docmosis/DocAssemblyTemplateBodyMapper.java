package uk.gov.hmcts.reform.ucmc.service.docmosis;

import uk.gov.hmcts.reform.ucmc.model.CaseData;
import uk.gov.hmcts.reform.ucmc.service.docmosis.model.DocAssemblyTemplateBody;

public abstract class DocAssemblyTemplateBodyMapper {

    public abstract DocAssemblyTemplateBody from(CaseData caseData);
}
