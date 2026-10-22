package com.argus.llmgateway.domain.ports.inbound;

import com.argus.llmgateway.domain.valueobjects.LlmResponse;

public interface CompleteUseCase {
    LlmResponse complete(CompleteCommand command);
}