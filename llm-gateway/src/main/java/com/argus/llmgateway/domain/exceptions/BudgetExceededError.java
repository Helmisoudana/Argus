package com.argus.llmgateway.domain.exceptions;



import java.util.UUID;

public final class BudgetExceededError extends LlmGatewayError {

    public BudgetExceededError(UUID runId, long tokensUsed, double costUsed) {
        super(LlmGatewayErrorCode.BUDGET_EXCEEDED,
                "Budget dépassé pour le run " + runId
                        + " (tokens=" + tokensUsed + ", coût=" + costUsed + ")");
    }
}