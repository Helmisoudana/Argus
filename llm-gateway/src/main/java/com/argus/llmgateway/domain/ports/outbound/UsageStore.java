package com.argus.llmgateway.domain.ports.outbound;

import com.argus.llmgateway.domain.entities.LlmCall;

import java.util.List;
import java.util.UUID;

public interface UsageStore {
    void save(LlmCall call);
    List<LlmCall> findByRun(UUID runId);
}