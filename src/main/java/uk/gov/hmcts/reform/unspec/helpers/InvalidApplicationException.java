package uk.gov.hmcts.reform.unspec.helpers;

public class InvalidApplicationException extends RuntimeException {
    public InvalidApplicationException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
