package com.argus.llmgateway.domain.valueobjects;


import com.argus.llmgateway.domain.exceptions.InvalidLlmRequestError;

public record Budget(long maxTokens, double maxCost) {

    public Budget {
        if (maxTokens <= 0) throw new InvalidLlmRequestError("maxTokens", "doit être positif");
        if (maxCost <= 0) throw new InvalidLlmRequestError("maxCost", "doit être positif");
    }
}