package com.argus.llmgateway.domain.entities;
import com.argus.llmgateway.domain.valueobjects.TokenUsage;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public record LlmCall(UUID id,
                      UUID runId,
                      String modelName,
                      TokenUsage usage,
                      double cost,
                      Duration duration,
                      Instant at) {
}