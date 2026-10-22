package com.argus.llmgateway.domain.entities;


import com.argus.llmgateway.domain.exceptions.BudgetExceededError;
import com.argus.llmgateway.domain.valueobjects.Budget;
import com.argus.llmgateway.domain.valueobjects.TokenUsage;

import java.util.UUID;

/** Consommation cumulée d'un run. Thread-safe : des appels parallèles sont possibles. */
public final class RunBudget {

    private final UUID runId;
    private final Budget limit;
    private long tokens;
    private double cost;

    public RunBudget(UUID runId, Budget limit) {
        this.runId = runId;
        this.limit = limit;
    }

    public synchronized void assertNotExceeded() {
        if (tokens >= limit.maxTokens() || cost >= limit.maxCost()) {
            throw new BudgetExceededError(runId, tokens, cost);
        }
    }

    public synchronized void consume(TokenUsage usage, double callCost) {
        tokens += usage.total();
        cost += callCost;
    }

    public synchronized long tokensUsed() {
        return tokens;
    }

    public synchronized double costUsed() {
        return cost;
    }
}