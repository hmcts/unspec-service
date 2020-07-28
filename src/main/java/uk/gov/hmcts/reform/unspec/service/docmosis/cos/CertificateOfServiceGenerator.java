package uk.gov.hmcts.reform.unspec.service.docmosis.cos;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.unspec.model.Address;
import uk.gov.hmcts.reform.unspec.model.CaseData;
import uk.gov.hmcts.reform.unspec.model.docmosis.DocmosisDocument;
import uk.gov.hmcts.reform.unspec.model.docmosis.cos.CertificateOfServiceForm;
import uk.gov.hmcts.reform.unspec.model.docmosis.sealedclaim.Representative;
import uk.gov.hmcts.reform.unspec.model.documents.CaseDocument;
import uk.gov.hmcts.reform.unspec.model.documents.DocumentType;
import uk.gov.hmcts.reform.unspec.model.documents.PDF;
import uk.gov.hmcts.reform.unspec.service.docmosis.DocumentGeneratorService;
import uk.gov.hmcts.reform.unspec.service.docmosis.TemplateDataGenerator;
import uk.gov.hmcts.reform.unspec.service.documentmanagement.DocumentManagementService;
import uk.gov.hmcts.reform.unspec.utils.CaseNameUtils;

import java.time.LocalDate;

import static uk.gov.hmcts.reform.unspec.service.docmosis.DocmosisTemplates.N215;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CertificateOfServiceGenerator extends TemplateDataGenerator<CertificateOfServiceForm> {

    private static final Representative TEMP_REPRESENTATIVE = Representative.builder()
        .contactName("MiguelSpooner")
        .dxAddress("DX 751Newport")
        .organisationName("DBE Law")
        .phoneNumber("0800 206 1592")
        .emailAddress("jim.smith@slatergordon.com")
        .serviceAddress(Address.builder()
                            .addressLine1("AdmiralHouse")
                            .addressLine2("Queensway")
                            .postTown("Newport")
                            .postCode("NP204AG")
                            .build())
        .build(); //TODO Rep details need to be picked from reference data
    public static final String REFERENCE_NUMBER = "000LR001"; //TODO Need to agree a way to get

    private final DocumentManagementService documentManagementService;
    private final DocumentGeneratorService documentGeneratorService;

    public CaseDocument generate(CaseData caseData, String authorisation) {
        CertificateOfServiceForm templateData = getTemplateData(caseData);

        DocmosisDocument docmosisDocument = documentGeneratorService.generateDocmosisDocument(templateData, N215);
        return documentManagementService.uploadDocument(
            authorisation,
            new PDF(getFileName(caseData), docmosisDocument.getBytes(), DocumentType.CERTIFICATE_OF_SERVICE)
        );
    }

    private String getFileName(CaseData caseData) {
        return String.format(N215.getDocumentTitle(), caseData.getLegacyCaseReference());
    }

    @Override
    public CertificateOfServiceForm getTemplateData(CaseData caseData) {
        return CertificateOfServiceForm.builder()
            .caseName(CaseNameUtils.toCaseName.apply(caseData))
            .referenceNumber(REFERENCE_NUMBER)
            .solicitorReferences(caseData.getSolicitorReferences())
            .issueDate(caseData.getClaimIssuedDate())
            .submittedOn(LocalDate.of(2020, 9, 29))
            .applicantName(CaseNameUtils.fetchClaimantName(caseData))
            .respondentName(CaseNameUtils.fetchDefendantName(caseData))
            .respondentRepresentative(TEMP_REPRESENTATIVE)
            .serviceMethod(caseData.getServiceMethod().name())
            .servedLocation(caseData.getServiceLocation().getLocation())
            .documentsServed("TODO:")
            .statementOfTruth(caseData.getClaimStatementOfTruth())
            .applicantRepresentative(TEMP_REPRESENTATIVE)
            .build();
    }
}
