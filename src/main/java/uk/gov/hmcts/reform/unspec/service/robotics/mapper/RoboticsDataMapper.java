package uk.gov.hmcts.reform.unspec.service.robotics.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.unspec.enums.AllocatedTrack;
import uk.gov.hmcts.reform.unspec.model.CaseData;
import uk.gov.hmcts.reform.unspec.model.LitigationFriend;
import uk.gov.hmcts.reform.unspec.model.Party;
import uk.gov.hmcts.reform.unspec.model.SolicitorReferences;
import uk.gov.hmcts.reform.unspec.model.robotics.CaseHeader;
import uk.gov.hmcts.reform.unspec.model.robotics.ClaimDetails;
import uk.gov.hmcts.reform.unspec.model.robotics.LitigiousParty;
import uk.gov.hmcts.reform.unspec.model.robotics.RoboticsCaseData;
import uk.gov.hmcts.reform.unspec.model.robotics.Solicitor;
import uk.gov.hmcts.reform.unspec.utils.PartyUtils;

import java.util.List;
import java.util.Optional;

import static java.time.format.DateTimeFormatter.ISO_DATE;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static uk.gov.hmcts.reform.unspec.utils.MonetaryConversions.penniesToPounds;

/**
 * This class is skeleton to be refined after we have final version of RPA Json structure
 * and it's mapping with CaseData.
 */
@Service
@RequiredArgsConstructor
public class RoboticsDataMapper {

    private final RoboticsAddressMapper addressMapper;
    private final EventHistoryMapper eventHistoryMapper;

    public RoboticsCaseData toRoboticsCaseData(CaseData caseData) {
        requireNonNull(caseData);
        return RoboticsCaseData.builder()
            .header(buildCaseHeader(caseData))
            .litigiousParties(buildLitigiousParties(caseData))
            .solicitors(buildSolicitors(caseData))
            .claimDetails(buildClaimDetails(caseData))
            .events(eventHistoryMapper.buildEvents(caseData))
            .build();
    }

    private ClaimDetails buildClaimDetails(CaseData caseData) {
        return ClaimDetails.builder()
            .amountClaimed(caseData.getClaimValue().toPounds())
            .courtFee(ofNullable(caseData.getClaimFee())
                          .map(fee -> penniesToPounds(fee.getCalculatedAmountInPence()))
                          .orElse(null))
            .caseIssuedDate(Optional.ofNullable(caseData.getClaimIssuedDate())
                                .map(issueDate -> issueDate.format(ISO_DATE))
                                .orElse(null))
            .caseRequestReceivedDate(caseData.getClaimSubmittedDateTime().toLocalDate().format(ISO_DATE))
            .build();
    }

    private CaseHeader buildCaseHeader(CaseData caseData) {
        return CaseHeader.builder()
            .caseNumber(caseData.getLegacyCaseReference())
            .caseType(caseData.getClaimType().name().replace("_", " "))
            .preferredCourtName(caseData.getCourtLocation().getApplicantPreferredCourt())
            .caseAllocatedTo(buildAllocatedTrack(caseData.getAllocatedTrack()))
            .build();
    }

    private String buildAllocatedTrack(AllocatedTrack allocatedTrack) {
        switch (allocatedTrack) {
            case FAST_CLAIM:
                return "FAST TRACK";
            case MULTI_CLAIM:
                return "MULTI TRACK";
            case SMALL_CLAIM:
                return "SMALL CLAIM TRACK";
            default:
                return "";
        }
    }

    private List<Solicitor> buildSolicitors(CaseData caseData) {
        return List.of(buildApplicantSolicitor(caseData), buildRespondentSolicitor(caseData));
    }

    private Solicitor buildRespondentSolicitor(CaseData caseData) {
        return Solicitor.builder()
            .reference(ofNullable(caseData.getSolicitorReferences())
                           .map(SolicitorReferences::getRespondentSolicitor1Reference)
                           .orElse(null)
            )
            .build();
    }

    private Solicitor buildApplicantSolicitor(CaseData caseData) {
        return Solicitor.builder()
            .reference(ofNullable(caseData.getSolicitorReferences())
                           .map(SolicitorReferences::getApplicantSolicitor1Reference)
                           .orElse(null)
            )
            .build();
    }

    private List<LitigiousParty> buildLitigiousParties(CaseData caseData) {
        return List.of(
            buildLitigiousParty(
                caseData.getApplicant1(),
                caseData.getApplicant1LitigationFriend(),
                "Claimant",
                "1"
            ),
            buildLitigiousParty(
                caseData.getRespondent1(),
                caseData.getRespondent1LitigationFriend(),
                "Defendant",
                "1"
            )
        );
    }

    private LitigiousParty buildLitigiousParty(Party party, LitigationFriend litigationFriend, String type, String id) {
        return LitigiousParty.builder()
            .id(id)
            .type(type)
            .name(PartyUtils.getLitigiousPartyName(party, litigationFriend))
            .dateOfBirth(PartyUtils.getDateOfBirth(party).map(d -> d.format(ISO_DATE)).orElse(null))
            .addresses(addressMapper.toRoboticsAddresses(party.getPrimaryAddress()))
            .build();
    }
}
