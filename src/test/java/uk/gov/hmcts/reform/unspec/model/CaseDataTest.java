package uk.gov.hmcts.reform.unspec.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.hmcts.reform.unspec.helpers.JsonMapper;

import java.io.IOException;
import java.util.Map;

import static uk.gov.hmcts.reform.unspec.utils.ResourceReader.readString;

@SpringBootTest
class CaseDataTest {

    @Autowired
    private JsonMapper mapper;

    @Test
    void shouldCreateExpectedCaseDataWhenReadingFromJson() throws IOException, JSONException {
        String data = readString("case_data.json");
        CaseData caseData = mapper.fromJson(data, CaseData.class);
        String dataAfterMapping = mapper.toJson(caseData);

        JSONAssert.assertEquals(data, dataAfterMapping, true);
    }

    @Test
    void shouldNotErrorWhenMappingEmptyCaseData() throws JsonProcessingException, JSONException {
        CaseData caseData = mapper.fromMap(Map.of(), CaseData.class);
        String dataAfterMapping = mapper.toJson(caseData);

        JSONAssert.assertEquals(Map.of().toString(), dataAfterMapping, true);
    }
}
