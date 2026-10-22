package com.argus.llmgateway.infrastructure.config;

import com.argus.llmgateway.domain.valueobjects.TaskType;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.Map;

@ConfigurationProperties(prefix = "argus.llm")
public record LlmGatewayProperties(Ollama ollama, RunBudget runBudget) {

    public record Ollama(String baseUrl, Duration timeout, Map<TaskType, String> models) { }

    public record RunBudget(long maxTokens, double maxCost) { }
}