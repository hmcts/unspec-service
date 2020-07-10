package uk.gov.hmcts.reform.ucmc.service.documentmanagement;

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
import uk.gov.hmcts.reform.ucmc.helpers.LocalDateTimeHelper;
import uk.gov.hmcts.reform.ucmc.model.documents.CaseDocument;
import uk.gov.hmcts.reform.ucmc.model.documents.PDF;
import uk.gov.hmcts.reform.ucmc.service.UserService;

import java.net.URI;
import java.util.List;

import static java.util.Collections.singletonList;

@Service
public class DocumentManagementService {

    private final Logger logger = LoggerFactory.getLogger(DocumentManagementService.class);
    private static final String FILES_NAME = "files";
    private static final String UNSPEC = "Unspecified";

    private final DocumentMetadataDownloadClientApi documentMetadataDownloadClient;
    private final DocumentDownloadClientApi documentDownloadClient;
    private final DocumentUploadClientApi documentUploadClient;
    private final AuthTokenGenerator authTokenGenerator;
    private final UserService userService;
    private final List<String> userRoles;

    @Autowired
    public DocumentManagementService(
        DocumentMetadataDownloadClientApi documentMetadataDownloadApi,
        DocumentDownloadClientApi documentDownloadClientApi,
        DocumentUploadClientApi documentUploadClientApi,
        AuthTokenGenerator authTokenGenerator,
        UserService userService,
        @Value("${document_management.userRoles}") List<String> userRoles
    ) {
        this.documentMetadataDownloadClient = documentMetadataDownloadApi;
        this.documentDownloadClient = documentDownloadClientApi;
        this.documentUploadClient = documentUploadClientApi;
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
            UploadResponse response = documentUploadClient.upload(
                authorisation,
                authTokenGenerator.generate(),
                userInfo.getUid(),
                userRoles,
                Classification.RESTRICTED,
                singletonList(file)
            );

            Document document = response.getEmbedded().getDocuments().stream()
                .findFirst()
                .orElseThrow(() ->
                     new DocumentManagementException("Document management failed uploading file" + originalFileName));

            return CaseDocument.builder()
                .documentLink(uk.gov.hmcts.reform.ucmc.model.documents.Document.builder()
                    .documentUrl(document.links.self.href)
                    .documentBinaryUrl(document.links.binary.href)
                    .documentFileName(originalFileName)
                    .build())
                .documentName(originalFileName)
                .documentType(pdf.getDocumentType())
                .createdDatetime(LocalDateTimeHelper.nowInUTC())
                .size(document.size)
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
    public CaseDocument logUploadDocumentFailure(
        DocumentManagementException exception,
        String authorisation,
        PDF pdf
    ) {
        String filename = pdf.getFilename();
        logger.info(exception.getMessage() + " " + exception.getCause(), exception);
        throw exception;
    }

    public byte[] downloadDocument(String authorisation, CaseDocument caseDocument) {
        return downloadDocumentByUrl(authorisation, URI.create(caseDocument.getDocumentLink().getDocumentUrl()));
    }

    @Retryable(value = DocumentManagementException.class, backoff = @Backoff(delay = 200))
    private byte[] downloadDocumentByUrl(String authorisation, URI documentUrl) {
        try {
            UserInfo userInfo = userService.getUserInfo(authorisation);
            String userRoles = String.join(",", this.userRoles);
            Document documentMetadata = documentMetadataDownloadClient.getDocumentMetadata(
                authorisation,
                authTokenGenerator.generate(),
                userRoles,
                userInfo.getUid(),
                documentUrl.getPath()
            );

            ResponseEntity<Resource> responseEntity = documentDownloadClient.downloadBinary(
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
                    documentUrl
                ), ex);
        }
    }

    @Recover
    public byte[] logDownloadDocumentFailure(
        DocumentManagementException exception,
        String authorisation,
        CaseDocument claimDocument
    ) {
        String filename = claimDocument.getDocumentName() + ".pdf";
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
