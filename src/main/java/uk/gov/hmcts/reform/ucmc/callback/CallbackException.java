package uk.gov.hmcts.reform.ucmc.callback;

public class CallbackException extends RuntimeException {
    public CallbackException(String message) {
        super(message);
    }

    public CallbackException(String message, Throwable t) {
        super(message, t);
    }
}
