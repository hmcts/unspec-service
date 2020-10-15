package uk.gov.hmcts.reform.unspec.model.dq;

import lombok.Builder;
import lombok.Setter;
import uk.gov.hmcts.reform.unspec.model.StatementOfTruth;
import uk.gov.hmcts.reform.unspec.model.documents.Document;

@Setter
@Builder
public class Applicant1DQ implements DQ {

    private final FileDirectionsQuestionnaire applicant1DQFileDirectionsQuestionnaire;
    private final DisclosureOfElectronicDocuments applicant1DQDisclosureOfElectronicDocuments;
    private final String applicant1DQDisclosureOfNonElectronicDocuments;
    private final DisclosureReport applicant1DQDisclosureReport;
    private final Experts applicant1DQExperts;
    private final Witnesses applicant1DQWitnesses;
    private final Hearing applicant1DQHearing;
    private final Document applicant1DQDraftDirections;
    private final RequestedCourt applicant1DQRequestedCourt;
    private final HearingSupport applicant1DQHearingSupport;
    private final FurtherInformation applicant1DQFurtherInformation;
    private final StatementOfTruth applicant1DQStatementOfTruth;

    @Override
    public FileDirectionsQuestionnaire getFileDirectionQuestionnaire() {
        return applicant1DQFileDirectionsQuestionnaire;
    }

    @Override
    public DisclosureOfElectronicDocuments getDisclosureOfElectronicDocuments() {
        return applicant1DQDisclosureOfElectronicDocuments;
    }

    @Override
    public String getDisclosureOfNonElectronicDocuments() {
        return applicant1DQDisclosureOfNonElectronicDocuments;
    }

    @Override
    public DisclosureReport getDisclosureReport() {
        return applicant1DQDisclosureReport;
    }

    @Override
    public Experts getExperts() {
        return applicant1DQExperts;
    }

    @Override
    public Witnesses getWitnesses() {
        return applicant1DQWitnesses;
    }

    @Override
    public Hearing getHearing() {
        return applicant1DQHearing;
    }

    @Override
    public Document getDraftDirections() {
        return applicant1DQDraftDirections;
    }

    @Override
    public RequestedCourt getRequestedCourt() {
        return null;
    }

    @Override
    public HearingSupport getHearingSupport() {
        return applicant1DQHearingSupport;
    }

    @Override
    public FurtherInformation getFurtherInformation() {
        return applicant1DQFurtherInformation;
    }

    @Override
    public StatementOfTruth getStatementOfTruth() {
        return applicant1DQStatementOfTruth;
    }
}
