package net.forje.solicitor;

public class SolicitorException extends Exception {

    public SolicitorException(final String message) {
        super(message);
    }

    public SolicitorException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public SolicitorException(final Throwable cause) {
        super(cause);
    }

    protected SolicitorException(final String message,
                                 final Throwable cause,
                                 final boolean enableSuppression,
                                 final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
