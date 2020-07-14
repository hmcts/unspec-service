package uk.gov.hmcts.reform.ucmc.service.docmosis.sealedclaim;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.ucmc.model.CaseData;
import uk.gov.hmcts.reform.ucmc.model.docmosis.DocmosisDocument;
import uk.gov.hmcts.reform.ucmc.model.docmosis.sealedclaim.SealedClaimForm;
import uk.gov.hmcts.reform.ucmc.model.documents.CaseDocument;
import uk.gov.hmcts.reform.ucmc.model.documents.DocumentType;
import uk.gov.hmcts.reform.ucmc.model.documents.PDF;
import uk.gov.hmcts.reform.ucmc.service.docmosis.DocAssemblyService;
import uk.gov.hmcts.reform.ucmc.service.docmosis.DocmosisTemplates;
import uk.gov.hmcts.reform.ucmc.service.docmosis.DocumentGeneratorService;
import uk.gov.hmcts.reform.ucmc.service.docmosis.TemplateDataGenerator;
import uk.gov.hmcts.reform.ucmc.service.documentmanagement.DocumentManagementService;

import java.util.List;

@Service
public class SealedClaimFormGenerator extends TemplateDataGenerator<SealedClaimForm> {

    private final SealedClaimTemplateBodyMapper sealedClaimTemplateBodyMapper;
    private final DocAssemblyService docAssemblyService;
    private final DocumentManagementService documentManagementService;
    private final DocumentGeneratorService documentGeneratorService;

    @Autowired
    public SealedClaimFormGenerator(SealedClaimTemplateBodyMapper sealedClaimTemplateBodyMapper,
                                    DocAssemblyService docAssemblyService,
                                    DocumentManagementService documentManagementService,
                                    DocumentGeneratorService documentGeneratorService) {
        this.sealedClaimTemplateBodyMapper = sealedClaimTemplateBodyMapper;
        this.docAssemblyService = docAssemblyService;
        this.documentManagementService = documentManagementService;
        this.documentGeneratorService = documentGeneratorService;
    }

    public CaseDocument generate(CaseData caseData, String authorisation) {
//        DocAssemblyTemplateBody body = sealedClaimTemplateBodyMapper.from(caseData);
//        Document docmosisDocument = docAssemblyService.generateDocument(
//            authorisation,
//            body,
//            DocmosisTemplates.OCCN1
//        );
        SealedClaimForm templateData = getTemplateData(caseData);
        DocmosisTemplates sealedClaimForm = DocmosisTemplates.OCCN1;
        DocmosisDocument docmosisDocument = documentGeneratorService.generateDocmosisDocument(
            templateData,
            sealedClaimForm
        );

        return documentManagementService.uploadDocument(
            authorisation,
            new PDF(sealedClaimForm.getDocumentTitle(), docmosisDocument.getBytes(), DocumentType.SEALED_CLAIM)
        );
    }

    @Override
    public SealedClaimForm getTemplateData(CaseData caseData) {
        return SealedClaimForm.builder()
            .claimant(List.of(caseData.getClaimant()))
            .defendants(List.of(caseData.getClaimant()))
            .claimAmount(caseData.getClaimValue().getHigherValue().toString())
            .statementOfTruth(caseData.getClaimStatementOfTruth())
            .build();
    }
}
