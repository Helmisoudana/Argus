package com.argus.llmgateway.domain.exceptions;

public final class InvalidLlmResponseError extends LlmGatewayError {

    public InvalidLlmResponseError(String reason) {
        super(LlmGatewayErrorCode.INVALID_RESPONSE, "Réponse du LLM invalide : " + reason);
    }
}