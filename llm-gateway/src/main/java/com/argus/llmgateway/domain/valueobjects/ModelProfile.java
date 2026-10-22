package com.argus.llmgateway.domain.valueobjects;
import com.argus.llmgateway.domain.exceptions.InvalidLlmRequestError;

public record ModelProfile(String provider,
                           String modelName,
                           boolean local,
                           int maxContextTokens,
                           double costPer1kTokens) {

    public ModelProfile {
        if (provider == null || provider.isBlank())
            throw new InvalidLlmRequestError("provider", "ne doit pas être vide");
        if (modelName == null || modelName.isBlank())
            throw new InvalidLlmRequestError("modelName", "ne doit pas être vide");
        if (maxContextTokens <= 0)
            throw new InvalidLlmRequestError("maxContextTokens", "doit être positif");
        if (costPer1kTokens < 0)
            throw new InvalidLlmRequestError("costPer1kTokens", "ne doit pas être négatif");
    }
}