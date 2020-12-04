package uk.gov.hmcts.reform.unspec;

import au.com.dius.pact.consumer.ConsumerPactBuilder;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Set;

import static au.com.dius.pact.consumer.ConsumerPactRunnerKt.runConsumerTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public abstract class BaseRpaTest {

    protected static final String PATH = "/fake-endpoint";
    protected static final String CONSUMER = "unspec_service";
    protected static final String PROVIDER = "rpa_api";

    protected ObjectMapper objectMapper = new ObjectMapper();

    protected RequestResponsePact preparePact(int statusCode, String description, String body) {
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

    protected void runPactTest(RequestResponsePact pact, PactTestRun pactTestRun) {
        MockProviderConfig config = MockProviderConfig.createDefault();
        PactVerificationResult result = runConsumerTest(pact, config, pactTestRun);

        if (result instanceof PactVerificationResult.Error) {
            throw new RuntimeException(((PactVerificationResult.Error) result).getError());
        }

        assertEquals(PactVerificationResult.Ok.INSTANCE, result);
    }

    protected PactTestRun preparePactTestRun(final String body) {
        // @formatter:off
        return mockServer -> RestAssured
            .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .body(body)
                .post(mockServer.getUrl() + PATH)
            .then()
                .statusCode(200);
        // @formatter:on
    }

    protected int validateJsonPayload(String body, String jsonSchemaFileName) {
        String jsonSchemaContents = readJsonSchema(jsonSchemaFileName);
        var jsonSchema = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7).getSchema(jsonSchemaContents);
        Set<ValidationMessage> errors = jsonSchema.validate(getJsonNodeFromStringContent(body));
        if (!errors.isEmpty()) {
            log.error("Schema validation errors: {}", errors);
            return 422;
        }
        return 200;
    }

    @SneakyThrows
    protected String writeToString(Object object) {
        return objectMapper.writeValueAsString(object);
    }

    @SneakyThrows
    protected JsonNode getJsonNodeFromStringContent(String content) {
        return objectMapper.readTree(content);
    }

    protected String readJsonSchema(String input) {
        try {
            URL resource = getClass().getResource(input);
            URI url = resource.toURI();
            return Files.readString(Paths.get(url));
        } catch (NoSuchFileException e) {
            throw new RuntimeException("no file found with the link '" + input + "'", e);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("failed to read from file '" + input + "'", e);
        }
    }
}
