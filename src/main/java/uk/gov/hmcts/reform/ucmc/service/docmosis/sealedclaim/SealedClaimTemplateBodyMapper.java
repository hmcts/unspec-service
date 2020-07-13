package uk.gov.hmcts.reform.ucmc.service.docmosis.sealedclaim;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.ucmc.model.CaseData;
import uk.gov.hmcts.reform.ucmc.service.docmosis.DocAssemblyTemplateBodyMapper;
import uk.gov.hmcts.reform.ucmc.service.docmosis.model.DocAssemblyTemplateBody;

import java.util.List;

@Component
public class SealedClaimTemplateBodyMapper extends DocAssemblyTemplateBodyMapper {
    @Override
    public DocAssemblyTemplateBody from(CaseData caseData) {
        return DocAssemblyTemplateBody.builder()
            .claimant(List.of(caseData.getClaimant()))
            .defendants(List.of(caseData.getClaimant()))
            .claimAmount(caseData.getClaimValue().getHigherValue().toString())
            .statementOfTruth(caseData.getClaimStatementOfTruth())
            .build();
    }
}
