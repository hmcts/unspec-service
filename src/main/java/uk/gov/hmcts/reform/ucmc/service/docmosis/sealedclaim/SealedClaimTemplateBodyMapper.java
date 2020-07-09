package uk.gov.hmcts.reform.ucmc.service.docmosis.sealedclaim;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.ucmc.model.CaseData;
import uk.gov.hmcts.reform.ucmc.service.docmosis.model.DocAssemblyTemplateBody;
import uk.gov.hmcts.reform.ucmc.service.docmosis.DocAssemblyTemplateBodyMapper;

@Component
public class SealedClaimTemplateBodyMapper extends DocAssemblyTemplateBodyMapper {
    @Override
    public DocAssemblyTemplateBody from(CaseData caseData) {
        return null;
    }
}
