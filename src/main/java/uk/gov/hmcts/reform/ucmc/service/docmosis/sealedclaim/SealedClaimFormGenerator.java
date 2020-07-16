package uk.gov.hmcts.reform.ucmc.service.docmosis.sealedclaim;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.ucmc.model.CaseData;
import uk.gov.hmcts.reform.ucmc.model.docmosis.DocmosisDocument;
import uk.gov.hmcts.reform.ucmc.model.docmosis.sealedclaim.SealedClaimForm;
import uk.gov.hmcts.reform.ucmc.model.documents.CaseDocument;
import uk.gov.hmcts.reform.ucmc.model.documents.DocumentType;
import uk.gov.hmcts.reform.ucmc.model.documents.PDF;
import uk.gov.hmcts.reform.ucmc.service.docmosis.DocmosisTemplates;
import uk.gov.hmcts.reform.ucmc.service.docmosis.DocumentGeneratorService;
import uk.gov.hmcts.reform.ucmc.service.docmosis.TemplateDataGenerator;
import uk.gov.hmcts.reform.ucmc.service.documentmanagement.DocumentManagementService;

import java.util.List;

@Service
public class SealedClaimFormGenerator extends TemplateDataGenerator<SealedClaimForm> {

    private final DocumentManagementService documentManagementService;
    private final DocumentGeneratorService documentGeneratorService;

    @Autowired
    public SealedClaimFormGenerator(DocumentManagementService documentManagementService,
                                    DocumentGeneratorService documentGeneratorService) {
        this.documentManagementService = documentManagementService;
        this.documentGeneratorService = documentGeneratorService;
    }

    public CaseDocument generate(CaseData caseData, String authorisation) {
        SealedClaimForm templateData = getTemplateData(caseData);
        DocmosisTemplates sealedClaimForm = DocmosisTemplates.N1;

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
            .defendants(List.of(caseData.getRespondent()))
            .claimValue(caseData.getClaimValue().formData()) //TODO Claim amount logic
            .statementOfTruth(caseData.getStatementOfTruth())
            .build();
    }
}
