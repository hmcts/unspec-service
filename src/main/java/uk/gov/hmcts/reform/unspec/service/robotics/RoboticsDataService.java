package uk.gov.hmcts.reform.unspec.service.robotics;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.unspec.model.CaseData;
import uk.gov.hmcts.reform.unspec.model.robotics.CaseHeader;
import uk.gov.hmcts.reform.unspec.model.robotics.ClaimDetails;
import uk.gov.hmcts.reform.unspec.model.robotics.Mediation;
import uk.gov.hmcts.reform.unspec.model.robotics.RoboticsCaseData;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoboticsDataService {

    public RoboticsCaseData toRoboticsCaseData(CaseData caseData) {
        return RoboticsCaseData.builder()
            .header(CaseHeader.builder()
                        .caseNumber(caseData.getLegacyCaseReference())
                        .preferredCourtName(caseData.getCourtLocation().getApplicantPreferredCourt())
                        .build())
            .litigiousParties(List.of())
            .solicitors(List.of())
            .claimDetails(ClaimDetails.builder()
                              .amountClaimed(caseData.getClaimValue().getStatementOfValueInPennies())
                              .build())
            .mediation(Mediation.builder().build())
            .build();
    }
}
