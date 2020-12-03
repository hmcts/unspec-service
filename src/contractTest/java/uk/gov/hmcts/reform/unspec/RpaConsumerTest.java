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
    void shouldGeneratePact_whenRoboticsObjectIsPopulated() {
        String description = "a request to fake endpoint with Robotics data for RPA";
        String rpaJson = writeToString(buildRoboticsCaseData());
        int statusCode = validateJsonPayload(rpaJson, "/rpa-json-schema.json");

        String payload = writeToString(buildRpaContract());
        RequestResponsePact pact = preparePact(statusCode, description, payload);
        PactTestRun pactTestRun = preparePactTestRun(payload);

        runPactTest(pact, pactTestRun);
    }

    private RpaContract buildRpaContract() {
        return RpaContract.builder()
            .title("A sample RPA Json for drop 1")
            .version("1.0.0")
            .payload(buildRoboticsCaseData())
            .build();
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
