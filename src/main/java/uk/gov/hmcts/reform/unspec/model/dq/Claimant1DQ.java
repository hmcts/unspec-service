package uk.gov.hmcts.reform.unspec.model.dq;

import lombok.Builder;
import lombok.Setter;
import uk.gov.hmcts.reform.unspec.model.StatementOfTruth;
import uk.gov.hmcts.reform.unspec.model.documents.Document;

@Setter
@Builder
public class Claimant1DQ implements DQ {

    private final FileDirectionsQuestionnaire claimant1DQFileDirectionsQuestionnaire;
    private final DisclosureOfElectronicDocuments claimant1DQDisclosureOfElectronicDocuments;
    private final String claimant1DQDisclosureOfNonElectronicDocuments;
    private final DisclosureReport claimant1DQDisclosureReport;
    private final Experts claimant1DQExperts;
    private final Witnesses claimant1DQWitnesses;
    private final Hearing claimant1DQHearing;
    private final Document claimant1DQDraftDirections;
    private final RequestedCourt claimant1DQRequestedCourt;
    private final HearingSupport claimant1DQHearingSupport;
    private final FurtherInformation claimant1DQFurtherInformation;
    private final StatementOfTruth claimant1DQStatementOfTruth;

    @Override
    public FileDirectionsQuestionnaire getFileDirectionQuestionnaire() {
        return claimant1DQFileDirectionsQuestionnaire;
    }

    @Override
    public DisclosureOfElectronicDocuments getDisclosureOfElectronicDocuments() {
        return claimant1DQDisclosureOfElectronicDocuments;
    }

    @Override
    public String getDisclosureOfNonElectronicDocuments() {
        return claimant1DQDisclosureOfNonElectronicDocuments;
    }

    @Override
    public DisclosureReport getDisclosureReport() {
        return claimant1DQDisclosureReport;
    }

    @Override
    public Experts getExperts() {
        return claimant1DQExperts;
    }

    @Override
    public Witnesses getWitnesses() {
        return claimant1DQWitnesses;
    }

    @Override
    public Hearing getHearing() {
        return claimant1DQHearing;
    }

    @Override
    public Document getDraftDirections() {
        return claimant1DQDraftDirections;
    }

    @Override
    public RequestedCourt getRequestedCourt() {
        return null;
    }

    @Override
    public HearingSupport getHearingSupport() {
        return claimant1DQHearingSupport;
    }

    @Override
    public FurtherInformation getFurtherInformation() {
        return claimant1DQFurtherInformation;
    }

    @Override
    public StatementOfTruth getStatementOfTruth() {
        return claimant1DQStatementOfTruth;
    }
}
