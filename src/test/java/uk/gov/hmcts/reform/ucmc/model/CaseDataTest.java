package uk.gov.hmcts.reform.ucmc.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.io.InputStream;

import static uk.gov.hmcts.reform.ucmc.model.CaseDataTest.ResourceReader.readString;

@SpringBootTest(classes = JacksonAutoConfiguration.class)
class CaseDataTest {

    @Autowired
    private ObjectMapper mapper;

    @Test
    void shouldCreateExpectedCaseData() throws IOException, JSONException {
        String data = readString("case_data.json");
        CaseData caseData = mapper.readValue(data, CaseData.class);
        String dataAfterMapping = mapper.writeValueAsString(caseData);

        JSONAssert.assertEquals(data, dataAfterMapping, true);
    }

    //TODO: move to own class
    public static class ResourceReader {

        private ResourceReader() {
            // NO-OP
        }

        public static String readString(String resourcePath) {
            return new String(ResourceReader.readBytes(resourcePath));
        }

        public static byte[] readBytes(String resourcePath) {
            try (InputStream inputStream = ResourceReader.class.getClassLoader().getResourceAsStream(resourcePath)) {
                if (inputStream == null) {
                    throw new IllegalArgumentException("Resource does not exist");
                }
                return IOUtils.toByteArray(inputStream);
            } catch (IOException ex) {
                throw new IllegalArgumentException(ex);
            }
        }
    }
}
