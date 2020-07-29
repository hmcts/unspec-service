package uk.gov.hmcts.reform.unspec.service.docmosis.cos;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.unspec.model.CaseData;
import uk.gov.hmcts.reform.unspec.model.docmosis.DocmosisData;
import uk.gov.hmcts.reform.unspec.model.docmosis.DocmosisDocument;
import uk.gov.hmcts.reform.unspec.model.documents.CaseDocument;
import uk.gov.hmcts.reform.unspec.model.documents.Document;
import uk.gov.hmcts.reform.unspec.model.documents.PDF;
import uk.gov.hmcts.reform.unspec.service.docmosis.DocumentGeneratorService;
import uk.gov.hmcts.reform.unspec.service.documentmanagement.DocumentManagementService;
import uk.gov.hmcts.reform.unspec.utils.ResourceReader;

import java.time.LocalDateTime;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.unspec.callback.CallbackHandlerFactoryTest.BEARER_TOKEN;
import static uk.gov.hmcts.reform.unspec.model.documents.DocumentType.CERTIFICATE_OF_SERVICE;
import static uk.gov.hmcts.reform.unspec.service.docmosis.DocmosisTemplates.N215;
import static uk.gov.hmcts.reform.unspec.service.documentmanagement.DocumentManagementService.UNSPEC;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
    CertificateOfServiceGenerator.class,
    JacksonAutoConfiguration.class
})
class CertificateOfServiceGeneratorTest {

    public static final String REFERENCE_NUMBER = "000LR095";
    private final byte[] bytes = {1, 2, 3, 4, 5, 6};
    private final String fileName = format(N215.getDocumentTitle(), REFERENCE_NUMBER);

    @MockBean
    private DocumentManagementService documentManagementService;
    @MockBean
    private DocumentGeneratorService documentGeneratorService;

    @Autowired
    private CertificateOfServiceGenerator certificateOfServiceGenerator;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @SneakyThrows
    void shouldGenerateCertificateOfService() {

        when(documentGeneratorService.generateDocmosisDocument(any(DocmosisData.class), eq(N215)))
            .thenReturn(new DocmosisDocument(N215.getDocumentTitle(), bytes));

        when(documentManagementService
                 .uploadDocument(eq(BEARER_TOKEN), eq(new PDF(fileName, bytes, CERTIFICATE_OF_SERVICE))))
            .thenReturn(getCaseDocument());

        CaseDocument caseDocument = certificateOfServiceGenerator.generate(getCaseData(), BEARER_TOKEN);
        assertThat(caseDocument).isNotNull().isEqualTo(getCaseDocument());

        verify(documentManagementService)
            .uploadDocument(eq(BEARER_TOKEN), eq(new PDF(fileName, bytes, CERTIFICATE_OF_SERVICE)));
        verify(documentGeneratorService)
            .generateDocmosisDocument(any(DocmosisData.class), eq(N215));
    }

    private CaseData getCaseData() throws JsonProcessingException {
        return objectMapper.readValue(ResourceReader.readString("case_data.json"), CaseData.class);
    }

    private CaseDocument getCaseDocument() {

        return CaseDocument.builder()
            .documentLink(Document.builder()
                              .documentFileName(fileName)
                              .documentBinaryUrl(
                                  "http://dm-store:4506/documents/73526424-8434-4b1f-acca-bd33a3f8338f/binary")
                              .documentUrl("http://dm-store:4506/documents/73526424-8434-4b1f-acca-bd33a3f8338f")
                              .build())
            .documentSize(56975)
            .createdDatetime(LocalDateTime.of(2020, 07, 16, 14, 05, 15, 550439))
            .documentType(CERTIFICATE_OF_SERVICE)
            .createdBy(UNSPEC)
            .documentName(fileName)
            .build();
    }
}
