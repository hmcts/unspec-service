package uk.gov.hmcts.reform.ucmc.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Map;

import static uk.gov.hmcts.reform.ucmc.utils.ResourceReader.readString;

@SpringBootTest(classes = JacksonAutoConfiguration.class)
class CaseDataTest {

    @Autowired
    private ObjectMapper mapper;

    @Test
    void shouldCreateExpectedCaseDataWhenReadingFromJson() throws IOException, JSONException {
        String data = readString("case_data.json");
        CaseData caseData = mapper.readValue(data, CaseData.class);
        String dataAfterMapping = mapper.writeValueAsString(caseData);

        JSONAssert.assertEquals(data, dataAfterMapping, true);
    }

    @Test
    void shouldNotErrorWhenMappingEmptyCaseData() throws JsonProcessingException, JSONException {
        CaseData caseData = mapper.convertValue(Map.of(), CaseData.class);
        String dataAfterMapping = mapper.writeValueAsString(caseData);

        JSONAssert.assertEquals(Map.of().toString(), dataAfterMapping, true);
    }
}
