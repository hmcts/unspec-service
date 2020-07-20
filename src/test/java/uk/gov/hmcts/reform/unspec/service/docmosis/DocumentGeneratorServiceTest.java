package uk.gov.hmcts.reform.unspec.service.docmosis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.unspec.config.DocmosisConfiguration;
import uk.gov.hmcts.reform.unspec.model.docmosis.DocmosisDocument;
import uk.gov.hmcts.reform.unspec.model.docmosis.DocmosisRequest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.unspec.service.docmosis.DocmosisTemplates.N1;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {JacksonAutoConfiguration.class})
class DocumentGeneratorServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ResponseEntity<byte[]> tornadoResponse;

    @Mock
    private DocmosisConfiguration configuration;

    @Captor
    ArgumentCaptor<HttpEntity<DocmosisRequest>> argumentCaptor;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void shouldInvokesTornado() {
        Map<String, Object> placeholders = getTemplatePlaceholders();

        when(restTemplate.exchange(eq(configuration.getUrl() + "/api/render"),
                                   eq(HttpMethod.POST), argumentCaptor.capture(), eq(byte[].class)
        )).thenReturn(tornadoResponse);

        byte[] expectedResponse = {1, 2, 3};
        when(tornadoResponse.getBody()).thenReturn(expectedResponse);

        DocmosisDocument docmosisDocument = createServiceInstance().generateDocmosisDocument(placeholders, N1);
        assertThat(docmosisDocument.getBytes()).isEqualTo(expectedResponse);

        assertThat(argumentCaptor.getValue().getBody().getTemplateName()).isEqualTo(N1.getTemplate());
        assertThat(argumentCaptor.getValue().getBody().getOutputFormat()).isEqualTo("pdf");
    }

    private Map<String, Object> getTemplatePlaceholders() {
        return Map.of(
            "jurisdiction", "PUBLICLAW",
            "familyManCaseNumber", "123",
            "todaysDate", "1 Jan 2019",
            "applicantName", "Bran Stark, Sansa Stark",
            "orderTypes", "EPO",
            "childrenNames", "Robb Stark, Jon Snow",
            "hearingDate", "2 Jan 2019",
            "hearingVenue", "Aldgate Tower floor 3",
            "preHearingAttendance", "",
            "hearingTime", "09.00pm"
        );
    }

    private DocumentGeneratorService createServiceInstance() {
        return new DocumentGeneratorService(restTemplate, configuration, mapper);
    }
}
