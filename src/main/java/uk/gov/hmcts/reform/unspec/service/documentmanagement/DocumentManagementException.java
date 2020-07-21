package uk.gov.hmcts.reform.unspec.service.documentmanagement;

public class DocumentManagementException extends RuntimeException {

    public DocumentManagementException(String message) {
        super(message);
    }

    public DocumentManagementException(String message, Throwable t) {
        super(message, t);
    }
}
