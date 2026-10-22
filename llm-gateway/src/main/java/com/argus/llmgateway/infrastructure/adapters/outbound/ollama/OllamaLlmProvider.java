package com.argus.llmgateway.infrastructure.adapters.outbound.ollama;

import com.argus.llmgateway.domain.exceptions.InvalidLlmResponseError;
import com.argus.llmgateway.domain.exceptions.ProviderUnavailableError;
import com.argus.llmgateway.domain.ports.outbound.LlmProvider;
import com.argus.llmgateway.domain.valueobjects.LlmResponse;
import com.argus.llmgateway.domain.valueobjects.ModelProfile;
import com.argus.llmgateway.domain.valueobjects.TokenUsage;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class OllamaLlmProvider implements LlmProvider {

    private static final String PROVIDER = "ollama";

    private final RestClient client;

    public OllamaLlmProvider(RestClient client) {
        this.client = client;
    }

    record ChatRequest(String model, List<Message> messages, boolean stream, Map<String, Object> options) { }

    record Message(String role, String content) { }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record ChatResponse(Message message,
                        @JsonProperty("prompt_eval_count") int promptEvalCount,
                        @JsonProperty("eval_count") int evalCount,
                        @JsonProperty("total_duration") long totalDuration) { }

    @Override
    public LlmResponse generate(ModelProfile model, String systemPrompt, String userPrompt) {
        ChatRequest body = new ChatRequest(
                model.modelName(),
                List.of(new Message("system", systemPrompt), new Message("user", userPrompt)),
                false,
                Map.of("temperature", 0));   // 0 = sorties plus reproductibles

        ChatResponse response;
        try {
            response = client.post()
                    .uri("/api/chat")
                    .body(body)
                    .retrieve()
                    .body(ChatResponse.class);
        } catch (RestClientException e) {
            throw new ProviderUnavailableError(PROVIDER, e);
        }

        if (response == null || response.message() == null || response.message().content() == null) {
            throw new InvalidLlmResponseError("réponse Ollama vide");
        }
        return new LlmResponse(
                response.message().content(),
                new TokenUsage(response.promptEvalCount(), response.evalCount()),
                model.modelName(),
                Duration.ofNanos(response.totalDuration()));
    }
}