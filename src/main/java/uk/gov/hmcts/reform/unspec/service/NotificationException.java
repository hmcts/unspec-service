package uk.gov.hmcts.reform.unspec.service;

public class NotificationException extends RuntimeException {

    public NotificationException(String message) {
        super(message);
    }

    public NotificationException(Exception cause) {
        super(cause);
    }

}
