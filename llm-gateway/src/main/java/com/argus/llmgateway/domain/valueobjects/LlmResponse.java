package com.argus.llmgateway.domain.valueobjects;

import java.time.Duration;
import java.util.Objects;

public record LlmResponse(String text, TokenUsage usage, String modelName, Duration duration) {

    public LlmResponse {
        Objects.requireNonNull(text, "text");
        Objects.requireNonNull(usage, "usage");
        Objects.requireNonNull(modelName, "modelName");
        Objects.requireNonNull(duration, "duration");
    }
}