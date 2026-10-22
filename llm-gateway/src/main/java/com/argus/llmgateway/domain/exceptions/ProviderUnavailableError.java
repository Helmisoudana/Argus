package com.argus.llmgateway.domain.exceptions;


public final class ProviderUnavailableError extends LlmGatewayError {

    public ProviderUnavailableError(String provider, Throwable cause) {
        super(LlmGatewayErrorCode.PROVIDER_UNAVAILABLE,
                "Fournisseur LLM indisponible : " + provider, cause);
    }
}