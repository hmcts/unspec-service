package uk.gov.hmcts.reform.unspec.service.documentmanagement;

public class DocumentDownloadException extends RuntimeException {

    public static final String DOWNLOAD_FAILURE = "Unable to download document %s from document management.";

    public DocumentDownloadException(String fileName, Throwable t) {
        super(String.format(DOWNLOAD_FAILURE, fileName), t);
    }
}
