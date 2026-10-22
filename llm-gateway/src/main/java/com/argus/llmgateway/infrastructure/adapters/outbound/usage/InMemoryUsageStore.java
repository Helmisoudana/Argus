package com.argus.llmgateway.infrastructure.adapters.outbound.usage;

import com.argus.llmgateway.domain.entities.LlmCall;
import com.argus.llmgateway.domain.ports.outbound.UsageStore;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class InMemoryUsageStore implements UsageStore {

    private final Map<UUID, List<LlmCall>> byRun = new ConcurrentHashMap<>();

    @Override
    public void save(LlmCall call) {
        byRun.computeIfAbsent(call.runId(), id -> new CopyOnWriteArrayList<>()).add(call);
    }

    @Override
    public List<LlmCall> findByRun(UUID runId) {
        return List.copyOf(byRun.getOrDefault(runId, List.of()));
    }
}