package uk.gov.hmcts.reform.unspec.service.robotics;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.unspec.service.robotics.exception.JsonSchemaValidationException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Set;

import static java.lang.String.format;

@Slf4j
@Service
public class JsonSchemaValidationService {

    private static final String JSON_SCHEMA_FILE = "/schema/rpa-json-schema.json";

    public boolean isValid(String body) {
        return isValid(body, JSON_SCHEMA_FILE);
    }

    public boolean isValid(String body, String jsonSchemaFileName) {
        Set<ValidationMessage> errors = validate(body, jsonSchemaFileName);
        if (!errors.isEmpty()) {
            log.error("Schema validation errors: {}", errors);
            return false;
        }
        return true;
    }

    public Set<ValidationMessage> validate(String payload) {
        return validate(payload, JSON_SCHEMA_FILE);
    }

    public Set<ValidationMessage> validate(String body, String jsonSchemaFileName) {
        String jsonSchemaContents = readJsonSchema(jsonSchemaFileName);
        var jsonSchema = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7).getSchema(jsonSchemaContents);
        Set<ValidationMessage> errors = jsonSchema.validate(getJsonNodeFromStringContent(body));
        return errors;
    }

    private JsonNode getJsonNodeFromStringContent(String content) {
        try {
            return new ObjectMapper().readTree(content);
        } catch (JsonProcessingException e) {
            throw new JsonSchemaValidationException(e.getMessage(), e);
        }
    }

    private String readJsonSchema(String input) {
        try {
            URL resource = getClass().getResource(input);
            URI url = resource.toURI();
            return Files.readString(Paths.get(url));
        } catch (NoSuchFileException e) {
            throw new JsonSchemaValidationException(format("no file found with the link '%s'", input), e);
        } catch (IOException | URISyntaxException e) {
            throw new JsonSchemaValidationException(format("failed to read from file '%s'", input), e);
        }
    }

    public String getJsonSchemaFile() {
        return JSON_SCHEMA_FILE;
    }
}
