package uk.gov.hmcts.reform.unspec;


import au.com.dius.pact.consumer.PactTestRun;
import au.com.dius.pact.model.RequestResponsePact;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.unspec.model.robotics.CaseHeader;
import uk.gov.hmcts.reform.unspec.model.robotics.ClaimDetails;
import uk.gov.hmcts.reform.unspec.model.robotics.RoboticsCaseData;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
class RpaConsumerTest extends BaseRpaTest {

    @Test
    @SneakyThrows
    void pactTest_whenRoboticsObjectIsPopulated() {

        int statusCode;
        String description = "a request to fake endpoint with Robotics data for RPA";

        final String body = createRequestBody(buildRoboticsCaseData());

        statusCode = validateJsonPayload(body, "/rpa-json-schema.json");
        RequestResponsePact pact = preparePact(statusCode, description, body);
        PactTestRun pactTestRun = preparePactTestRun(body);
        runPactTest(pact, pactTestRun);
    }

    private RoboticsCaseData buildRoboticsCaseData() {
        return RoboticsCaseData.builder()
            .header(CaseHeader.builder()
                        .caseNumber("000LR001")
                        .caseType("Fast Track")
                        .preferredCourtCode("Preferred court name")
                        .build())
            .litigiousParties(List.of())
            .solicitors(List.of())
            .claimDetails(ClaimDetails.builder()
                              .courtFee(BigDecimal.ONE)
                              .amountClaimed(BigDecimal.TEN)
                              .build())
            .build();
    }
}
