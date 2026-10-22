package com.argus.llmgateway.domain.exceptions;



public final class PrivacyViolationError extends LlmGatewayError {

    public PrivacyViolationError(String provider) {
        super(LlmGatewayErrorCode.PRIVACY_VIOLATION,
                "Le fournisseur '" + provider + "' n'est pas local, interdit par la politique LOCAL_ONLY");
    }
}