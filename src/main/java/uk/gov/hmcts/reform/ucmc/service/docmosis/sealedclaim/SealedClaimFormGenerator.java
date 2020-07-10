package uk.gov.hmcts.reform.ucmc.service.docmosis.sealedclaim;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.ucmc.helpers.LocalDateTimeHelper;
import uk.gov.hmcts.reform.ucmc.model.CaseData;
import uk.gov.hmcts.reform.ucmc.model.documents.CaseDocument;
import uk.gov.hmcts.reform.ucmc.model.documents.Document;
import uk.gov.hmcts.reform.ucmc.model.documents.DocumentType;
import uk.gov.hmcts.reform.ucmc.service.docmosis.DocAssemblyService;
import uk.gov.hmcts.reform.ucmc.service.docmosis.model.DocAssemblyTemplateBody;
import uk.gov.hmcts.reform.ucmc.service.docmosis.DocmosisTemplates;
import uk.gov.hmcts.reform.ucmc.service.documentmanagement.DocumentManagementService;

import java.time.LocalDateTime;

@Service
public class SealedClaimFormGenerator {

    private final SealedClaimTemplateBodyMapper sealedClaimTemplateBodyMapper;
    private final DocAssemblyService docAssemblyService;
    private final DocumentManagementService documentManagementService;

    @Autowired
    public SealedClaimFormGenerator(SealedClaimTemplateBodyMapper sealedClaimTemplateBodyMapper,
                                    DocAssemblyService docAssemblyService,
                                    DocumentManagementService documentManagementService) {
        this.sealedClaimTemplateBodyMapper = sealedClaimTemplateBodyMapper;
        this.docAssemblyService = docAssemblyService;
        this.documentManagementService = documentManagementService;
    }

    public CaseDocument generate(CaseData caseData, String authorisation) {
        DocAssemblyTemplateBody body = sealedClaimTemplateBodyMapper.from(caseData);
        Document docmosisDocument = docAssemblyService.generateDocument(
            authorisation,
            body,
            DocmosisTemplates.OCCN1
        );

        uk.gov.hmcts.reform.document.domain.Document metaData = documentManagementService.getDocumentMetaData(
            authorisation,
            docmosisDocument.getDocumentUrl()
        );

        return CaseDocument.builder()
            .documentLink(docmosisDocument.toBuilder().documentBinaryUrl(metaData.links.binary.href).build())
            .createdDatetime(LocalDateTimeHelper.nowInUTC())
            .createdBy(metaData.createdBy)
            .documentType(DocumentType.SEALED_CLAIM)
            .size(metaData.size)
            .documentName(docmosisDocument.getDocumentFileName())
            .build();

    }
}
