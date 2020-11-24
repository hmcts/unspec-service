package uk.gov.hmcts.reform.unspec;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.model.RequestResponsePact;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import lombok.SneakyThrows;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.unspec.model.robotics.CaseHeader;
import uk.gov.hmcts.reform.unspec.model.robotics.ClaimDetails;
import uk.gov.hmcts.reform.unspec.model.robotics.RoboticsCaseData;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(PactConsumerTestExt.class)
@ExtendWith(SpringExtension.class)
public class RpaConsumerTest {

    private static final RoboticsCaseData ROBOTICS_CASE_DATA = RoboticsCaseData.builder()
        .header(CaseHeader.builder()
                    .caseNumber("000LR001")
                    .caseType("Fast Track")
                    .preferredCourtCode("Preferred court name")
                    .build())
        .claimDetails(ClaimDetails.builder()
                          .courtFee(BigDecimal.ONE)
                          .amountClaimed(BigDecimal.TEN)
                          .build())
        .build();
    private static final String FAKE_ENDPOINT_1 = "/fake-endpoint";

    @Pact(provider = "rpa_api_1", consumer = "unspec_service")
    public RequestResponsePact executePostAndGet200(PactDslWithProvider builder) {
        // @formatter:off
        return builder
            .given("there is RPA Json object")
            .uponReceiving("a POST /fake-endpoint request ")
                .path(FAKE_ENDPOINT_1)
                .body(this::createRequestBody, MediaType.APPLICATION_JSON_VALUE)
                .method(HttpMethod.POST.toString())
                .headers(Map.of())
            .willRespondWith()
                .status(HttpStatus.OK.value())
                .matchHeader("Content-Type", "application/json; charset=utf-8")
                .body(createRpaPactDslJsonBody())
            .toPact();
        // @formatter:on
    }

    @Test
    @PactTestFor(pactMethod = "executePostAndGet200")
    public void shouldGetRpa_WhenPostOnFakeEndpoint(MockServer mockServer) throws JSONException {
        Map<String, String> headers = new HashMap<>();

        // @formatter:off
        String responseBody = RestAssured
            .given()
                .headers(headers)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .body(createRequestBody())
                .post(mockServer.getUrl() + FAKE_ENDPOINT_1)
            .then()
                .statusCode(200)
                .and()
                .extract()
                .body()
                .asString();
        // @formatter:on

        JSONObject response = new JSONObject(responseBody);

        assertThat(responseBody).isNotNull();
        assertThat(response.has("header")).isTrue();
        assertThat(response.getJSONObject("header").getString("caseNumber")).isNotBlank();
        assertThat(response.getJSONObject("header").getString("caseType")).isNotBlank();
        assertThat(response.has("claimDetails")).isTrue();
        assertThat(response.getJSONObject("claimDetails").getString("courtFee")).isNotBlank();
        assertThat(response.getJSONObject("claimDetails").getString("amountClaimed")).isNotBlank();
    }

    private PactDslJsonBody createRpaPactDslJsonBody() {
        PactDslJsonBody jsonBody = new PactDslJsonBody()
            .object("header")
            .stringType("caseNumber", ROBOTICS_CASE_DATA.getHeader().getCaseNumber())
            .stringType("caseType", ROBOTICS_CASE_DATA.getHeader().getCaseType())
            .closeObject()
            .object("claimDetails")
            .stringType("courtFee", ROBOTICS_CASE_DATA.getClaimDetails().getCourtFee().toString())
            .stringType("amountClaimed", ROBOTICS_CASE_DATA.getClaimDetails().getAmountClaimed().toString())
            .closeObject()
            .asBody();
        return jsonBody;
    }

    @SneakyThrows
    private String createRequestBody() {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(ROBOTICS_CASE_DATA);
    }
}
