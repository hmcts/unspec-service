package uk.gov.hmcts.reform.unspec;

import au.com.dius.pact.consumer.PactTestRun;
import au.com.dius.pact.consumer.PactVerificationResult;
import au.com.dius.pact.model.RequestResponsePact;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.unspec.model.CaseData;
import uk.gov.hmcts.reform.unspec.model.robotics.RoboticsCaseData;
import uk.gov.hmcts.reform.unspec.sampledata.CaseDataBuilder;
import uk.gov.hmcts.reform.unspec.service.flowstate.FlowState;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.hmcts.reform.unspec.matcher.IsValidJson.validateJson;

@Slf4j
class RpaConsumerTest extends BaseRpaTest {

    @Test
    @SneakyThrows
    void shouldGeneratePact_whenRoboticsObjectIsPopulated() {
        String description = "a request to fake endpoint with Robotics data for RPA";
        CaseData caseData = CaseDataBuilder.builder().atState(FlowState.Main.CLAIM_STAYED).build();
        RoboticsCaseData roboticsCaseData = roboticsDataMapper.toRoboticsCaseData(caseData);
        String payload = toJsonString(roboticsCaseData);

        assertThat(payload, validateJson());

        Map<String, String> headers = Map.of("title", "Case Stayed RPA Json", "version", "1.0.0");
        RequestResponsePact pact = preparePact(description, payload, headers);
        PactTestRun pactTestRun = preparePactTestRun(payload, headers);

        var result = runPactTest(pact, pactTestRun);

        assertEquals(PactVerificationResult.Ok.INSTANCE, result);
    }
}
