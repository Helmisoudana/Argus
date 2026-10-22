package com.argus.llmgateway.domain.valueobjects;

import com.argus.llmgateway.domain.exceptions.InvalidLlmRequestError;

public record TokenUsage(int promptTokens, int completionTokens) {

    public TokenUsage {
        if (promptTokens < 0) throw new InvalidLlmRequestError("promptTokens", "négatif");
        if (completionTokens < 0) throw new InvalidLlmRequestError("completionTokens", "négatif");
    }

    public int total() {
        return promptTokens + completionTokens;
    }
}