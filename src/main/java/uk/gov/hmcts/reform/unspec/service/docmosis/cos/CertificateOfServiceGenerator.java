package uk.gov.hmcts.reform.unspec.service.docmosis.cos;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.unspec.enums.ServedDocuments;
import uk.gov.hmcts.reform.unspec.enums.ServiceLocationType;
import uk.gov.hmcts.reform.unspec.model.Address;
import uk.gov.hmcts.reform.unspec.model.CaseData;
import uk.gov.hmcts.reform.unspec.model.ServiceLocation;
import uk.gov.hmcts.reform.unspec.model.SolicitorReferences;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static uk.gov.hmcts.reform.unspec.enums.ServedDocuments.OTHER;
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
    public static final String TEMP_REFERENCE_NUMBER = "000LR001"; //TODO Need to agree a way to get
    public static final LocalDate TEMP_SUBMITTED_ON = LocalDate.now(); //TODO: Not populated yet in case data

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
            .referenceNumber(TEMP_REFERENCE_NUMBER)
            .solicitorReferences(prepareSolicitorReferences(caseData.getSolicitorReferences()))
            .issueDate(caseData.getClaimIssuedDate())
            .submittedOn(TEMP_SUBMITTED_ON)
            .applicantName(CaseNameUtils.fetchClaimantName(caseData))
            .respondentName(CaseNameUtils.fetchDefendantName(caseData))
            .respondentRepresentative(TEMP_REPRESENTATIVE)
            .serviceMethod(caseData.getServiceMethod().getLabel())
            .servedLocation(prepareServedLocation(caseData.getServiceLocation()))
            .documentsServed(prepareDocumentList(caseData.getServedDocuments(), caseData.getServedDocumentsOther()))
            .statementOfTruth(caseData.getClaimStatementOfTruth())
            .applicantRepresentative(TEMP_REPRESENTATIVE)
            .build();
    }

    private SolicitorReferences prepareSolicitorReferences(SolicitorReferences solicitorReferences) {
        return SolicitorReferences
            .builder()
            .claimantReference(ofNullable(solicitorReferences.getClaimantReference()).orElse("Not Provided"))
            .defendantReference(ofNullable(solicitorReferences.getDefendantReference()).orElse("Not Provided"))
            .build();
    }

    private String prepareServedLocation(ServiceLocation serviceLocation) {
        if (serviceLocation.getLocation() == ServiceLocationType.OTHER) {
            return ServiceLocationType.OTHER.getLabel() + " - " + serviceLocation.getOther();
        }
        return serviceLocation.getLocation().getLabel();
    }

    private String prepareDocumentList(List<ServedDocuments> servedDocuments, String otherServedDocuments) {
        return servedDocuments.stream()
            .map(ServedDocuments::getLabel)
            .map(label -> label.replace(OTHER.getLabel(), "Other - " + otherServedDocuments))
            .collect(Collectors.joining("\n"));
    }
}
