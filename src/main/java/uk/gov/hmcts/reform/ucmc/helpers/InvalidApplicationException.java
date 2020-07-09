package uk.gov.hmcts.reform.ucmc.helpers;

public class InvalidApplicationException extends RuntimeException {
    public InvalidApplicationException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
