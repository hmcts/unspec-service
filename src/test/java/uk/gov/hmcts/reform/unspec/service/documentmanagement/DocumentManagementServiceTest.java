package uk.gov.hmcts.reform.unspec.service.documentmanagement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.document.DocumentDownloadClientApi;
import uk.gov.hmcts.reform.document.DocumentMetadataDownloadClientApi;
import uk.gov.hmcts.reform.document.DocumentUploadClientApi;
import uk.gov.hmcts.reform.document.domain.Classification;
import uk.gov.hmcts.reform.document.domain.Document;
import uk.gov.hmcts.reform.document.domain.UploadResponse;
import uk.gov.hmcts.reform.idam.client.models.UserInfo;
import uk.gov.hmcts.reform.unspec.model.documents.CaseDocument;
import uk.gov.hmcts.reform.unspec.model.documents.PDF;
import uk.gov.hmcts.reform.unspec.service.UserService;

import java.net.URI;
import java.util.List;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.unspec.callback.CallbackHandlerFactoryTest.BEARER_TOKEN;
import static uk.gov.hmcts.reform.unspec.model.documents.DocumentType.SEALED_CLAIM;
import static uk.gov.hmcts.reform.unspec.utils.ResourceReader.readString;

@SpringBootTest(classes = {
    DocumentManagementService.class,
    JacksonAutoConfiguration.class
})
class DocumentManagementServiceTest {

    private static final List<String> USER_ROLES = List.of("caseworker-civil", "caseworker-civil-solicitor");
    private static final String USER_ROLES_JOINED = "caseworker-civil,caseworker-civil-solicitor";

    @MockBean
    private DocumentMetadataDownloadClientApi documentMetadataDownloadClient;
    @MockBean
    private DocumentDownloadClientApi documentDownloadClient;
    @MockBean
    private DocumentUploadClientApi documentUploadClient;
    @MockBean
    private AuthTokenGenerator authTokenGenerator;
    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private DocumentManagementService documentManagementService;

    @Mock
    private ResponseEntity<Resource> responseEntity;
    private final PDF document = new PDF("0000-claim", "test".getBytes(), SEALED_CLAIM);
    private final UserInfo userInfo = UserInfo.builder()
        .roles(List.of("role"))
        .uid("id")
        .givenName("userFirstName")
        .familyName("userLastName")
        .sub("mail@mail.com")
        .build();

    @BeforeEach
    public void setUp() {
        when(authTokenGenerator.generate()).thenReturn(BEARER_TOKEN);
        when(userService.getUserInfo(anyString())).thenReturn(userInfo);
    }

    @Nested
    class UploadDocument {

        @Test
        public void shouldUploadToDocumentManagement() throws JsonProcessingException {
            when(documentUploadClient.upload(
                anyString(),
                anyString(),
                anyString(),
                eq(USER_ROLES),
                any(Classification.class),
                anyList()
                 )
            ).thenReturn(mapper.readValue(
                readString("document-management/response.success.json"), UploadResponse.class)
            );

            CaseDocument caseDocument = documentManagementService.uploadDocument(BEARER_TOKEN, document);
            assertNotNull(caseDocument.getDocumentLink());
            assertEquals(
                "http://dm-store:4506/documents/85d97996-22a5-40d7-882e-3a382c8ae1b4",
                caseDocument.getDocumentLink().getDocumentUrl()
            );

            verify(documentUploadClient, atLeast(2))
                .upload(anyString(), anyString(), anyString(), eq(USER_ROLES), any(Classification.class), anyList());
        }

        @Test
        public void shouldThrow_whenUploadDocumentFails() throws JsonProcessingException {
            when(documentUploadClient.upload(
                anyString(),
                anyString(),
                anyString(),
                eq(USER_ROLES),
                any(Classification.class),
                anyList()
                 )
            ).thenReturn(mapper.readValue(
                readString("document-management/response.failure.json"), UploadResponse.class));

            DocumentManagementException documentManagementException = assertThrows(
                DocumentManagementException.class,
                () -> documentManagementService.uploadDocument(anyString(), document)
            );

            assertEquals(
                "Unable to upload document 0000-claim.pdf to document management.",
                documentManagementException.getMessage()
            );

            verify(documentUploadClient)
                .upload(anyString(), anyString(), anyString(), eq(USER_ROLES), any(Classification.class), anyList());
        }
    }

    @Nested
    class DownloadDocument {

        @Test
        public void shouldDownloadDocumentFromDocumentManagement() throws JsonProcessingException {

            when(documentMetadataDownloadClient
                     .getDocumentMetadata(anyString(), anyString(), eq(USER_ROLES_JOINED), anyString(), anyString())
            ).thenReturn(mapper.readValue(
                readString("document-management/download.success.json"), Document.class)
            );

            when(responseEntity.getBody()).thenReturn(new ByteArrayResource("test".getBytes()));

            when(documentDownloadClient
                     .downloadBinary(anyString(), anyString(), eq(USER_ROLES_JOINED), anyString(), anyString())
            ).thenReturn(responseEntity);

            URI documentUri = URI.create("http://dm-store:4506/documents/85d97996-22a5-40d7-882e-3a382c8ae1b4");

            byte[] pdf = documentManagementService.downloadDocument(BEARER_TOKEN, documentUri);

            assertNotNull(pdf);
            assertArrayEquals("test".getBytes(), pdf);

            verify(documentMetadataDownloadClient, atLeast(3))
                .getDocumentMetadata(anyString(), anyString(), eq(USER_ROLES_JOINED), anyString(), anyString());
            verify(documentDownloadClient)
                .downloadBinary(anyString(), anyString(), eq(USER_ROLES_JOINED), anyString(), anyString());
        }

        @Test
        public void shouldThrow_whenDocumentDownloadFails() {
            when(documentMetadataDownloadClient
                     .getDocumentMetadata(anyString(), anyString(), eq(USER_ROLES_JOINED), anyString(), anyString())
            ).thenReturn(null);

            URI documentUri = URI.create("http://dm-store:4506/documents/85d97996-22a5-40d7-882e-3a382c8ae1b4");

            DocumentManagementException documentManagementException = assertThrows(
                DocumentManagementException.class,
                () -> documentManagementService.downloadDocument("auth string", documentUri)
            );

            assertEquals(
                format("Unable to download document %s from document management.", documentUri),
                documentManagementException.getMessage()
            );

            verify(documentMetadataDownloadClient, atLeast(2))
                .getDocumentMetadata(anyString(), anyString(), eq(USER_ROLES_JOINED), anyString(), anyString());
        }
    }

    @Nested
    class DocumentMetaData {
        @Test
        public void getDocumentMetaData() throws JsonProcessingException {
            URI docUri = URI.create("http://dm-store:4506/documents/85d97996-22a5-40d7-882e-3a382c8ae1b4");

            when(documentMetadataDownloadClient
                     .getDocumentMetadata(anyString(), anyString(), eq(USER_ROLES_JOINED), anyString(), anyString())
            ).thenReturn(mapper.readValue(
                readString("document-management/download.success.json"), Document.class)
            );

            when(responseEntity.getBody()).thenReturn(new ByteArrayResource("test".getBytes()));

            Document documentMetaData = documentManagementService
                .getDocumentMetaData("auth string", docUri.getPath());

            assertEquals(72552L, documentMetaData.size);
            assertEquals("000LR002.pdf", documentMetaData.originalDocumentName);

            verify(documentMetadataDownloadClient)
                .getDocumentMetadata(anyString(), anyString(), eq(USER_ROLES_JOINED), anyString(), anyString());
        }

        @Test
        public void shouldThrow_whenMetadataDownloadFails() {
            when(documentMetadataDownloadClient
                     .getDocumentMetadata(anyString(), anyString(), eq(USER_ROLES_JOINED), anyString(), anyString())
            ).thenThrow(new RuntimeException("Failed to access document metadata"));

            URI documentUri = URI.create("http://dm-store:4506/documents/85d97996-22a5-40d7-882e-3a382c8ae1b4");

            DocumentManagementException documentManagementException = assertThrows(
                DocumentManagementException.class,
                () -> documentManagementService.getDocumentMetaData("auth string", documentUri.getPath())
            );

            assertEquals(
                "Unable to download document from document management.",
                documentManagementException.getMessage()
            );

            verify(documentMetadataDownloadClient)
                .getDocumentMetadata(anyString(), anyString(), eq(USER_ROLES_JOINED), anyString(), anyString());
        }
    }
}
