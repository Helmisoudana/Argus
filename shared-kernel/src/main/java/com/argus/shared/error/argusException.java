package com.argus.shared.error;

public abstract class argusException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final ErrorCode code;

    protected argusException(ErrorCode code, String message) {
        super(message);
        this.code = Guard.notNull(code, "code");
    }

    protected argusException(ErrorCode code, String message, Throwable cause) {
        super(message, cause);
        this.code = Guard.notNull(code, "code");
    }

    /** Code stable permettant de classer l'erreur sans analyser le message. */
    public ErrorCode code() {
        return code;
    }
}
