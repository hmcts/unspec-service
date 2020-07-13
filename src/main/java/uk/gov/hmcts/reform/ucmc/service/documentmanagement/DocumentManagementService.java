package uk.gov.hmcts.reform.ucmc.service.documentmanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.document.DocumentMetadataDownloadClientApi;
import uk.gov.hmcts.reform.document.domain.Document;
import uk.gov.hmcts.reform.idam.client.models.UserInfo;
import uk.gov.hmcts.reform.ucmc.service.UserService;

import java.util.List;

@Service
public class DocumentManagementService {

    private final DocumentMetadataDownloadClientApi documentMetadataDownloadClient;
    private final AuthTokenGenerator authTokenGenerator;
    private final UserService userService;
    private final List<String> userRoles;

    @Autowired
    public DocumentManagementService(
        DocumentMetadataDownloadClientApi documentMetadataDownloadApi,
        AuthTokenGenerator authTokenGenerator,
        UserService userService,
        @Value("${document_management.userRoles}") List<String> userRoles
    ) {
        this.documentMetadataDownloadClient = documentMetadataDownloadApi;
        this.authTokenGenerator = authTokenGenerator;
        this.userService = userService;
        this.userRoles = userRoles;
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
