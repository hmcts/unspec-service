package uk.gov.hmcts.reform.ucmc.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.gov.hmcts.reform.ucmc.Application;
import uk.gov.hmcts.reform.ucmc.service.UserService;

@SpringBootTest(
    classes = Application.class
)
@AutoConfigureMockMvc
@TestPropertySource(
    properties = {
        "core_case_data.api.url=false"
    }
)
public abstract class BaseIntegrationTest {

    protected static final String BEARER_TOKEN = "Bearer eyJ0eXAiOiJKV1QiLCJ6aXAiOiJOT05FIiwia2lkIjoiYi9PNk92VnYxK3k"
        + "rV2dySDVVaTlXVGlvTHQwPSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJzb2xpY2l0b3JAZXhhbXBsZS5jb20iLCJhdXRoX2xldmVsIjowLC"
        + "JhdWRpdFRyYWNraW5nSWQiOiJiNGJmMjJhMi02ZDFkLTRlYzYtODhlOS1mY2NhZDY2NjM2ZjgiLCJpc3MiOiJodHRwOi8vZnItYW06ODA4M"
        + "C9vcGVuYW0vb2F1dGgyL2htY3RzIiwidG9rZW5OYW1lIjoiYWNjZXNzX3Rva2VuIiwidG9rZW5fdHlwZSI6IkJlYXJlciIsImF1dGhHcmFu"
        + "dElkIjoiZjExMTk3MGQtMzQ3MS00YjY3LTkxMzYtZmYxYzk0MjMzMTZmIiwiYXVkIjoieHVpX3dlYmFwcCIsIm5iZiI6MTU5NDE5NzI3NCw"
        + "iZ3JhbnRfdHlwZSI6ImF1dGhvcml6YXRpb25fY29kZSIsInNjb3BlIjpbIm9wZW5pZCIsInByb2ZpbGUiLCJyb2xlcyIsImNyZWF0ZS11c2"
        + "VyIiwibWFuYWdlLXVzZXIiXSwiYXV0aF90aW1lIjoxNTk0MTk3MjczMDAwLCJyZWFsbSI6Ii9obWN0cyIsImV4cCI6MTU5NDIyNjA3NCwia"
        + "WF0IjoxNTk0MTk3Mjc0LCJleHBpcmVzX2luIjoyODgwMCwianRpIjoiYTJmNThmYzgtMmIwMy00M2I0LThkOTMtNmU0NWQyZTU0OTcxIn0."
        + "PTWXIvTTw54Ob1XYdP_x4l5-ITWDdbAY3-IPAPFkHDmjKgEVweabxrDIp2_RSoAcyZgza8LqJSTc00-_RzZ079nyl9pARy08BpljLZCmYdo"
        + "F2RO8CHuEVagF-SQdL37d-4pJPIMRChO0AmplBj1qMtVbuRd3WGNeUvoCtStdviFwlxvzRnLdHKwCi6AQHMaw1V9n9QyU9FxNSbwmNsCDt7"
        + "k02vLJDY9fLCsFYy5iWGCjb8lD1aX1NTv7jz2ttNNv7-smqp6L3LSSD_LCZMpf0h_3n5RXiv-N3vNpWe4ZC9u0AWQdHEE9QlKTZlsqwKSog"
        + "3yJWhyxAamdMepgW7Z8jQ";

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected UserService userService;

    @SneakyThrows
    protected <T> ResultActions doPost(String auth, T content, String urlTemplate, Object... uriVars) {
        return mockMvc.perform(
            MockMvcRequestBuilders.post(urlTemplate, uriVars)
                .header(HttpHeaders.AUTHORIZATION, auth)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(content)));
    }

    protected String toJson(Object input) {
        try {
            return objectMapper.writeValueAsString(input);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(
                String.format("Failed to serialize '%s' to JSON", input.getClass().getSimpleName()), e
            );
        }
    }
}
