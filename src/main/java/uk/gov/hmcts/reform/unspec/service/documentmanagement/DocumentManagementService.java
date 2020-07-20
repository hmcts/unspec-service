package uk.gov.hmcts.reform.unspec.service.documentmanagement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.document.DocumentDownloadClientApi;
import uk.gov.hmcts.reform.document.DocumentMetadataDownloadClientApi;
import uk.gov.hmcts.reform.document.DocumentUploadClientApi;
import uk.gov.hmcts.reform.document.domain.Classification;
import uk.gov.hmcts.reform.document.domain.Document;
import uk.gov.hmcts.reform.document.domain.UploadResponse;
import uk.gov.hmcts.reform.document.utils.InMemoryMultipartFile;
import uk.gov.hmcts.reform.idam.client.models.UserInfo;
import uk.gov.hmcts.reform.unspec.helpers.LocalDateTimeHelper;
import uk.gov.hmcts.reform.unspec.model.documents.CaseDocument;
import uk.gov.hmcts.reform.unspec.model.documents.PDF;
import uk.gov.hmcts.reform.unspec.service.UserService;

import java.net.URI;
import java.util.List;

import static java.util.Collections.singletonList;

@Service
public class DocumentManagementService {

    private final Logger logger = LoggerFactory.getLogger(DocumentManagementService.class);
    public static final String UNSPEC = "Unspec";
    private static final String FILES_NAME = "files";

    private final DocumentUploadClientApi documentUploadClientApi;
    private final DocumentDownloadClientApi documentDownloadClientApi;
    private final DocumentMetadataDownloadClientApi documentMetadataDownloadClient;
    private final AuthTokenGenerator authTokenGenerator;
    private final UserService userService;
    private final List<String> userRoles;

    @Autowired
    public DocumentManagementService(
        DocumentUploadClientApi documentUploadClientApi,
        DocumentDownloadClientApi documentDownloadClientApi,
        DocumentMetadataDownloadClientApi documentMetadataDownloadApi,
        AuthTokenGenerator authTokenGenerator,
        UserService userService,
        @Value("${document_management.userRoles}") List<String> userRoles
    ) {
        this.documentUploadClientApi = documentUploadClientApi;
        this.documentDownloadClientApi = documentDownloadClientApi;
        this.documentMetadataDownloadClient = documentMetadataDownloadApi;
        this.authTokenGenerator = authTokenGenerator;
        this.userService = userService;
        this.userRoles = userRoles;
    }


    @Retryable(value = {DocumentManagementException.class}, backoff = @Backoff(delay = 200))
    public CaseDocument uploadDocument(String authorisation, PDF pdf) {
        String originalFileName = pdf.getFilename();
        try {
            MultipartFile file
                = new InMemoryMultipartFile(FILES_NAME, originalFileName, PDF.CONTENT_TYPE, pdf.getBytes());

            UserInfo userInfo = userService.getUserInfo(authorisation);
            UploadResponse response = documentUploadClientApi.upload(
                authorisation,
                authTokenGenerator.generate(),
                userInfo.getUid(),
                userRoles,
                Classification.RESTRICTED,
                singletonList(file)
            );

            Document document = response.getEmbedded().getDocuments().stream()
                .findFirst()
                .orElseThrow(() -> {
                    String message = "Document management failed uploading file" + originalFileName;
                    return new DocumentManagementException(message);
                });

            return CaseDocument.builder()
                .documentLink(uk.gov.hmcts.reform.unspec.model.documents.Document.builder()
                                  .documentUrl(document.links.self.href)
                                  .documentBinaryUrl(document.links.binary.href)
                                  .documentFileName(originalFileName)
                                  .build())
                .documentName(originalFileName)
                .documentType(pdf.getDocumentType())
                .createdDatetime(LocalDateTimeHelper.nowInUTC())
                .documentSize(document.size)
                .createdBy(UNSPEC)
                .build();
        } catch (Exception ex) {
            throw new DocumentManagementException(String.format(
                "Unable to upload document %s to document management.",
                originalFileName
            ), ex);
        }
    }

    @Recover
    public CaseDocument logUploadDocumentFailure(DocumentManagementException exception,
                                                 String authorisation,
                                                 PDF pdf) {
        String filename = pdf.getFilename();
        logger.info(exception.getMessage() + " " + exception.getCause(), exception);
        throw exception;
    }

    @Retryable(value = DocumentManagementException.class, backoff = @Backoff(delay = 200))
    public byte[] downloadDocument(String authorisation, URI documentManagementUrl) {
        try {
            UserInfo userInfo = userService.getUserInfo(authorisation);
            String userRoles = String.join(",", this.userRoles);
            Document documentMetadata = getDocumentMetaData(authorisation, documentManagementUrl.getPath());

            ResponseEntity<Resource> responseEntity = documentDownloadClientApi.downloadBinary(
                authorisation,
                authTokenGenerator.generate(),
                userRoles,
                userInfo.getUid(),
                URI.create(documentMetadata.links.binary.href).getPath()
            );

            ByteArrayResource resource = (ByteArrayResource) responseEntity.getBody();
            //noinspection ConstantConditions let the NPE be thrown
            return resource.getByteArray();
        } catch (Exception ex) {
            throw new DocumentManagementException(
                String.format(
                    "Unable to download document %s from document management.",
                    documentManagementUrl
                ), ex);
        }
    }

    @Recover
    public byte[] logDownloadDocumentFailure(
        DocumentManagementException exception,
        String authorisation,
        CaseDocument caseDocument
    ) {
        String filename = caseDocument.getDocumentName() + ".pdf";
        logger.warn(exception.getMessage() + " " + exception.getCause(), exception);
        throw exception;
    }

    public Document getDocumentMetaData(String authorisation, String documentPath) {
        try {
            UserInfo userInfo = userService.getUserInfo(authorisation);
            String userRoles = String.join(",", this.userRoles);
            return documentMetadataDownloadClient.getDocumentMetadata(
                authorisation,
                authTokenGenerator.generate(),
                userRoles,
                userInfo.getUid(),
                documentPath
            );
        } catch (Exception ex) {
            throw new DocumentManagementException(
                "Unable to download document from document management.", ex);
        }
    }
}
