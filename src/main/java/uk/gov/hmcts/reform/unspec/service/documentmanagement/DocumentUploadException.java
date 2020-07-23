package uk.gov.hmcts.reform.unspec.service.documentmanagement;

public class DocumentUploadException extends RuntimeException {

    public static final String DOWNLOAD_FAILURE = "Unable to upload document %s to document management.";

    public DocumentUploadException(String fileName) {
        super(String.format(DOWNLOAD_FAILURE, fileName));
    }

    public DocumentUploadException(String fileName, Throwable t) {
        super(String.format(DOWNLOAD_FAILURE, fileName), t);
    }
}
