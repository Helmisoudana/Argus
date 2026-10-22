package com.argus.llmgateway.domain.exceptions;

public abstract class LlmGatewayError extends RuntimeException {

    private final LlmGatewayErrorCode code;

    protected LlmGatewayError(LlmGatewayErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    protected LlmGatewayError(LlmGatewayErrorCode code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public LlmGatewayErrorCode code() {
        return code;
    }
}