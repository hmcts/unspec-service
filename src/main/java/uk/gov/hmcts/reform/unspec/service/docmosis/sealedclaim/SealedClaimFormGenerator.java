package uk.gov.hmcts.reform.unspec.service.docmosis.sealedclaim;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.unspec.model.Address;
import uk.gov.hmcts.reform.unspec.model.CaseData;
import uk.gov.hmcts.reform.unspec.model.Party;
import uk.gov.hmcts.reform.unspec.model.docmosis.DocmosisDocument;
import uk.gov.hmcts.reform.unspec.model.docmosis.sealedclaim.Claimant;
import uk.gov.hmcts.reform.unspec.model.docmosis.sealedclaim.Defendant;
import uk.gov.hmcts.reform.unspec.model.docmosis.sealedclaim.Representative;
import uk.gov.hmcts.reform.unspec.model.docmosis.sealedclaim.SealedClaimForm;
import uk.gov.hmcts.reform.unspec.model.documents.CaseDocument;
import uk.gov.hmcts.reform.unspec.model.documents.DocumentType;
import uk.gov.hmcts.reform.unspec.model.documents.PDF;
import uk.gov.hmcts.reform.unspec.service.docmosis.DocumentGeneratorService;
import uk.gov.hmcts.reform.unspec.service.docmosis.TemplateDataGenerator;
import uk.gov.hmcts.reform.unspec.service.documentmanagement.DocumentManagementService;

import java.time.LocalDate;
import java.util.List;

import static uk.gov.hmcts.reform.unspec.service.docmosis.DocmosisTemplates.N1;

@Service
public class SealedClaimFormGenerator extends TemplateDataGenerator<SealedClaimForm> {

    public static final String TEMP_CLAIM_DETAILS = "The claimant seeks compensation from injuries and losses arising"
        + " from a road traffic accident which occurred on 1st July 2017 as a result of the negligence of the first "
        + "defendant.The claimant seeks compensation from injuries and losses arising from a road traffic accident "
        + "which occurred on 1st July 2017 as a result of the negligence of the first defendant."; //TODO

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
        .build(); //TODO
    public static final String REFERENCE_NUMBER = "000LR095"; //TODO
    public static final String CASE_NAME = "SamClark v AlexRichards"; //TODO
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

        DocmosisDocument docmosisDocument = documentGeneratorService.generateDocmosisDocument(templateData, N1);

        return documentManagementService.uploadDocument(
            authorisation,
            new PDF(getFileName(caseData), docmosisDocument.getBytes(), DocumentType.SEALED_CLAIM)
        );
    }

    private String getFileName(CaseData caseData) {
        return String.format(N1.getDocumentTitle(), caseData.getLegacyCaseReference());
    }

    @Override
    public SealedClaimForm getTemplateData(CaseData caseData) {
        return SealedClaimForm.builder()
            .claimants(getClaimants(caseData))
            .defendants(geDefendants(caseData))
            .claimValue(caseData.getClaimValue().formData())
            .statementOfTruth(caseData.getClaimStatementOfTruth())
            .claimDetails(TEMP_CLAIM_DETAILS)
            .hearingCourtLocation(caseData.getCourtLocation().getPreferredCourt())
            .claimantRepresentative(TEMP_REPRESENTATIVE)
            .referenceNumber(REFERENCE_NUMBER)
            .issueDate(LocalDate.of(2020, 9, 29))
            .submittedOn(LocalDate.of(2020, 9, 29))
            .claimantExternalReference(caseData.getSolicitorReferences().getClaimantReference())
            .defendantExternalReference(caseData.getSolicitorReferences().getClaimantReference())
            .caseName(CASE_NAME)
            .build();
    }

    private List<Defendant> geDefendants(CaseData caseData) {
        Party respondent = caseData.getRespondent();
        return List.of(Defendant.builder()
                           .name(getNameBasedOnType(respondent))
                           .primaryAddress(respondent.getPrimaryAddress())
                           .representative(TEMP_REPRESENTATIVE)
                           .build());
    }

    private List<Claimant> getClaimants(CaseData caseData) {
        Party applicant = caseData.getClaimant();
        return List.of(Claimant.builder()
                           .name(getNameBasedOnType(applicant))
                           .primaryAddress(applicant.getPrimaryAddress())
                           .build());
    }

    private String getNameBasedOnType(Party party) {
        switch (party.getType()) {
            //            case COMPANY:
            //                return party.getCompanyName();
            case INDIVIDUAL:
                return
                    getTitle(party.getIndividualTitle())
                        + party.getIndividualFirstName()
                        + " "
                        + party.getIndividualLastName();
            //            case SOLE_TRADER:
            //                return getTitle(party.getSoleTraderTitle())
            //                    + party.getSoleTraderFirstName()
            //                    + party.getSoleTraderLastName();
            //
            //            case ORGANISATION:
            //                return party.getOrganisationName();
            default:
                throw new IllegalArgumentException("invalid Applicant type " + party.getType());
        }
    }

    private String getTitle(String title) {
        return StringUtils.isBlank(title) ? "" : title + " ";
    }
}
