package uk.gov.hmcts.reform.unspec.assertion;

import uk.gov.hmcts.reform.unspec.model.CaseData;
import uk.gov.hmcts.reform.unspec.model.LitigationFriend;
import uk.gov.hmcts.reform.unspec.model.Party;
import uk.gov.hmcts.reform.unspec.model.robotics.CaseHeader;
import uk.gov.hmcts.reform.unspec.model.robotics.ClaimDetails;
import uk.gov.hmcts.reform.unspec.model.robotics.LitigiousParty;
import uk.gov.hmcts.reform.unspec.model.robotics.RoboticsCaseData;
import uk.gov.hmcts.reform.unspec.model.robotics.Solicitor;
import uk.gov.hmcts.reform.unspec.utils.PartyUtils;

import static java.time.format.DateTimeFormatter.ISO_DATE;
import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.gov.hmcts.reform.unspec.assertion.CustomAssertions.assertMoney;
import static uk.gov.hmcts.reform.unspec.assertion.CustomAssertions.assertThat;

public class RoboticsCaseDataAssert extends CustomAssert<RoboticsCaseDataAssert, RoboticsCaseData> {

    public RoboticsCaseDataAssert(RoboticsCaseData actual) {
        super("RoboticsCaseData", actual, RoboticsCaseDataAssert.class);
    }

    public RoboticsCaseDataAssert isEqualTo(CaseData expected) {
        isNotNull();

        CaseHeader header = actual.getHeader();
        compare(
            "caseNumber",
            expected.getLegacyCaseReference(),
            ofNullable(header.getCaseNumber())
        );
        compare(
            "preferredCourtName",
            expected.getCourtLocation().getApplicantPreferredCourt(),
            ofNullable(header.getPreferredCourtName())
        );

        assertClaimDetails(expected, actual.getClaimDetails());

        assertParty(
            "applicant1",
            "Claimant",
            actual.getLitigiousParties().get(0),
            expected.getApplicant1(),
            expected.getApplicant1LitigationFriend()
        );
        assertParty(
            "respondent1",
            "Defendant",
            actual.getLitigiousParties().get(1),
            expected.getRespondent1(),
            expected.getRespondent1LitigationFriend()
        );

        assertSolicitor(
            "applicant1" + "." + "reference",
            actual.getSolicitors().get(0),
            expected.getSolicitorReferences().getApplicantSolicitor1Reference()
        );
        assertSolicitor(
            "respondent1" + "." + "reference",
            actual.getSolicitors().get(1),
            expected.getSolicitorReferences().getRespondentSolicitor1Reference()
        );

        assertNotNull(actual.getEvents());

        return this;
    }

    private void assertClaimDetails(CaseData expected, ClaimDetails actual) {
        compare(
            "caseIssuedDate",
            expected.getClaimIssuedDate().format(ISO_DATE),
            ofNullable(actual.getCaseIssuedDate())
        );

        compare(
            "caseRequestReceivedDate",
            expected.getClaimSubmittedDateTime().toLocalDate().format(ISO_DATE),
            ofNullable(actual.getCaseRequestReceivedDate())
        );

        compare(
            "amountClaimed",
            expected.getClaimValue().toPounds(),
            ofNullable(actual.getAmountClaimed())
        );

        compare(
            "courtFee",
            ofNullable(expected.getClaimFee())
                .map(fee -> fee.getCalculatedAmountInPence())
                .orElse(null),
            ofNullable(actual.getCourtFee()),
            (e, a) -> assertMoney(a).isEqualTo(e)
        );
    }

    private void assertSolicitor(String fieldName, Solicitor solicitor, String reference) {
        compare(
            fieldName,
            solicitor.getReference(),
            ofNullable(reference)
        );
    }

    private void assertParty(String fieldName,
                             String litigiousPartyType,
                             LitigiousParty litigiousParty,
                             Party party,
                             LitigationFriend litigationFriend
    ) {
        if (party == null && litigiousParty != null) {
            failExpectedPresent(fieldName, litigiousParty);
            return;
        }

        if (party != null && litigiousParty == null) {
            failExpectedAbsent(fieldName, party);
            return;
        }

        compare(
            "name",
            litigiousParty.getName(),
            ofNullable(PartyUtils.getLitigiousPartyName(party, litigationFriend))
        );
        compare(
            "type",
            litigiousParty.getType(),
            ofNullable(litigiousPartyType)
        );
        compare(
            "dateOfBirth",
            litigiousParty.getDateOfBirth(),
            PartyUtils.getDateOfBirth(party).map(d -> d.format(ISO_DATE))
        );

        assertThat(litigiousParty.getAddresses().getContactAddress())
            .isEqualTo(party.getPrimaryAddress());
    }
}
