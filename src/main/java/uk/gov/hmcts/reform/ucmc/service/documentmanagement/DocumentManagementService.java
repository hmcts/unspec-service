package uk.gov.hmcts.reform.ucmc.service.documentmanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
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

    private static final String UNSPEC = "Unspec";
    private static final String FILES_NAME = "files";

    private final DocumentUploadClientApi documentUploadClientApi;
    private final DocumentMetadataDownloadClientApi documentMetadataDownloadClient;
    private final AuthTokenGenerator authTokenGenerator;
    private final UserService userService;
    private final List<String> userRoles;

    @Autowired
    public DocumentManagementService(
        DocumentUploadClientApi documentUploadClientApi,
        DocumentMetadataDownloadClientApi documentMetadataDownloadApi,
        AuthTokenGenerator authTokenGenerator,
        UserService userService,
        @Value("${document_management.userRoles}") List<String> userRoles
    ) {
        this.documentUploadClientApi = documentUploadClientApi;
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
            throw new DocumentManagementException(String.format("Unable to upload document %s to document management.",
                                                                originalFileName), ex);
        }
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
