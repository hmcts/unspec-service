package uk.gov.hmcts.reform.unspec;

import au.com.dius.pact.consumer.ConsumerPactBuilder;
import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.PactTestRun;
import au.com.dius.pact.consumer.PactVerificationResult;
import au.com.dius.pact.model.MockProviderConfig;
import au.com.dius.pact.model.RequestResponsePact;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import io.restassured.RestAssured;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.Set;

import static au.com.dius.pact.consumer.ConsumerPactRunnerKt.runConsumerTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RpaPactTest {

    private static final String PATH = "/fake-endpoint";
    private static final String CONSUMER = "unspec_service";
    private static final String PROVIDER = "rpa_api";
    private static final String JSON_SCHEMA = "{\"type\":\"object\",\"$id\": \"https://example.com\",\"properties\":{\"name\":{\"type\":[\"string\", \"null\"]},\"type\":{\"type\":[\"string\", \"null\"]}},\"additionalProperties\": false,\"required\": [ \"name\", \"type\" ]}";

    //    @ParameterizedTest
//    @CsvSource("hello1,hello2,hello3")
    @Test
    @SneakyThrows
    void pactTest_whenPartialObjectIsPopulated() {

        int statusCode;
        String description = "a request to fake endpoint with partial data";

        Robot robot = new Robot();
        robot.setType("Electric");

        final String body = createRequestBody(robot);
        statusCode = validateJsonPayload(body);
        RequestResponsePact pact = preparePact(statusCode, description, body);
        PactTestRun pactTestRun = preparePactTestRun(body);
        runPactTest(pact, pactTestRun);
    }

    @Test
    @SneakyThrows
    void pactTest_whenFullObjectIsPopulated() {

        int statusCode;
        String description = "a request to fake endpoint with full data";

        Robot robot = new Robot();
        robot.setType("Electric");
        robot.setName("Wallie");
        final String body = createRequestBody(robot);

        statusCode = validateJsonPayload(body);
        RequestResponsePact pact = preparePact(statusCode, description, body);
        PactTestRun pactTestRun = preparePactTestRun(body);
        runPactTest(pact, pactTestRun);
    }

    private void runPactTest(RequestResponsePact pact, PactTestRun pactTestRun) {
        MockProviderConfig config = MockProviderConfig.createDefault();
        PactVerificationResult result = runConsumerTest(pact, config, pactTestRun);

        if (result instanceof PactVerificationResult.Error) {
            throw new RuntimeException(((PactVerificationResult.Error) result).getError());
        }

        assertEquals(PactVerificationResult.Ok.INSTANCE, result);
    }

    private int validateJsonPayload(String body) {
        var jsonSchema = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4).getSchema(JSON_SCHEMA);
        Set<ValidationMessage> errors = jsonSchema.validate(getJsonNodeFromStringContent(body));
        System.out.println(errors);
        return errors.isEmpty() ? 200 : 422;
    }

    @NotNull
    private PactTestRun preparePactTestRun(final String body) {
        return new PactTestRun() {
            @Override
            public void run(@NotNull MockServer mockServer) throws IOException {
                RestAssured
                    .given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .body(body)
                    .post(mockServer.getUrl() + PATH)
                    .then()
                    .statusCode(200);
            }
        };
    }

    private RequestResponsePact preparePact(int statusCode, String description, String body) {
        // @formatter:off
        return ConsumerPactBuilder
            .consumer(CONSUMER)
            .hasPactWith(PROVIDER)
            .uponReceiving(description)
                .path(PATH)
                .method("POST")
                .body(body)
            .willRespondWith()
                .status(statusCode)
            .toPact();
        // @formatter:on
    }

    @SneakyThrows
    private String createRequestBody(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }

    @SneakyThrows
    JsonNode getJsonNodeFromStringContent(String content) {
        return new ObjectMapper().readTree(content);
    }
}

