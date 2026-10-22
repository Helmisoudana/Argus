package com.argus.llmgateway.domain.ports.inbound;


import com.argus.llmgateway.domain.exceptions.InvalidLlmRequestError;
import com.argus.llmgateway.domain.valueobjects.PrivacyPolicy;
import com.argus.llmgateway.domain.valueobjects.TaskType;

import java.util.Objects;
import java.util.UUID;

public record CompleteCommand(UUID runId,
                              TaskType task,
                              String systemPrompt,
                              String userPrompt,
                              PrivacyPolicy policy) {

    public CompleteCommand {
        Objects.requireNonNull(runId, "runId");
        Objects.requireNonNull(task, "task");
        Objects.requireNonNull(policy, "policy");
        if (userPrompt == null || userPrompt.isBlank()) {
            throw new InvalidLlmRequestError("userPrompt", "ne doit pas être vide");
        }
        systemPrompt = systemPrompt == null ? "" : systemPrompt;
    }
}