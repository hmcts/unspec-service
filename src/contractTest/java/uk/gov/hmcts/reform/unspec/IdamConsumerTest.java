package uk.gov.hmcts.reform.unspec;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.dsl.PactDslJsonArray;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.model.RequestResponsePact;
import io.restassured.RestAssured;
import org.apache.http.HttpHeaders;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(PactConsumerTestExt.class)
@ExtendWith(SpringExtension.class)
public class IdamConsumerTest {

    private static final String ACCESS_TOKEN = "111";
    private static final String IDAM_DETAILS_URL = "/details";

    @Pact(provider = "Idam_api", consumer = "unspec_service")
    public RequestResponsePact executeGetUserDetailsAndGet200(PactDslWithProvider builder) {

        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN);

        return builder
            .given("Idam successfully returns user details")
            .uponReceiving("Provider receives a GET /details request from uk.gov.hmcts.reform.unspec.unspec service")
            .path(IDAM_DETAILS_URL)
            .method(HttpMethod.GET.toString())
            .headers(headers)
            .willRespondWith()
            .status(HttpStatus.OK.value())
            .body(createUserDetailsResponse())
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "executeGetUserDetailsAndGet200")
    public void shouldGetUserDetails_WhenAccessToken(MockServer mockServer) throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN);

        String responseBody =
            RestAssured
                .given()
                .headers(headers)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(mockServer.getUrl() + IDAM_DETAILS_URL)
                .then()
                .statusCode(200)
                .and()
                .extract()
                .body()
                .asString();

        JSONObject response = new JSONObject(responseBody);

        assertThat(responseBody).isNotNull();
        assertThat(response).hasNoNullFieldsOrProperties();
        assertThat(response.getString("id")).isNotBlank();
        assertThat(response.getString("forename")).isNotBlank();
        assertThat(response.getString("surname")).isNotBlank();

        JSONArray rolesArr = new JSONArray(response.getString("roles"));

        assertThat(rolesArr).isNotNull();
        assertThat(rolesArr.length()).isNotZero();
        assertThat(rolesArr.get(0).toString()).isNotBlank();
    }

    private PactDslJsonBody createUserDetailsResponse() {
        PactDslJsonArray array = new PactDslJsonArray().stringValue("caseworker-cmc");

        return new PactDslJsonBody()
            .stringType("id", "123")
            .stringType("email", "uk.gov.hmcts.reform.unspec.unspec-solicitor@fake.hmcts.net")
            .stringType("forename", "John")
            .stringType("surname", "Smith")
            .stringType("roles", array.toString());
    }
}
