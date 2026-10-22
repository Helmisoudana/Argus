package com.argus.llmgateway.domain.ports.outbound;

import com.argus.llmgateway.domain.valueobjects.LlmResponse;
import com.argus.llmgateway.domain.valueobjects.ModelProfile;

public interface LlmProvider {
    LlmResponse generate(ModelProfile model, String systemPrompt, String userPrompt);
}