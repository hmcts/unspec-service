package uk.gov.hmcts.reform.ucmc.service.docmosis.sealedclaim;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.ucmc.model.CaseData;
import uk.gov.hmcts.reform.ucmc.model.DocmosisDocument;
import uk.gov.hmcts.reform.ucmc.service.docmosis.DocAssemblyService;
import uk.gov.hmcts.reform.ucmc.service.docmosis.model.DocAssemblyTemplateBody;
import uk.gov.hmcts.reform.ucmc.service.docmosis.DocmosisTemplates;

@Service
public class SealedClaimFormGenerator {

    private final SealedClaimTemplateBodyMapper sealedClaimTemplateBodyMapper;
    private final DocAssemblyService docAssemblyService;

    @Autowired
    public SealedClaimFormGenerator(SealedClaimTemplateBodyMapper sealedClaimTemplateBodyMapper,
                                    DocAssemblyService docAssemblyService) {
        this.sealedClaimTemplateBodyMapper = sealedClaimTemplateBodyMapper;
        this.docAssemblyService = docAssemblyService;
    }

    public DocmosisDocument generate(CaseData caseData, String authorisation) {
        DocAssemblyTemplateBody body = sealedClaimTemplateBodyMapper.from(caseData);
        return docAssemblyService.generateDocument(
            authorisation,
            body,
            DocmosisTemplates.OCCN1
        );
    }
}
