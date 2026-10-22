package com.argus.llmgateway.domain.exceptions;

public final class InvalidLlmRequestError extends LlmGatewayError {

    private final String field;

    public InvalidLlmRequestError(String field, String reason) {
        super(LlmGatewayErrorCode.INVALID_INPUT, field + " : " + reason);
        this.field = field;
    }

    public String field() {
        return field;
    }
}