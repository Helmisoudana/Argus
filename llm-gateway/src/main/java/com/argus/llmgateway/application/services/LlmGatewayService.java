package com.argus.llmgateway.application.services;

import com.argus.llmgateway.domain.entities.LlmCall;
import com.argus.llmgateway.domain.entities.RunBudget;
import com.argus.llmgateway.domain.ports.inbound.CompleteCommand;
import com.argus.llmgateway.domain.ports.inbound.CompleteStructuredUseCase;
import com.argus.llmgateway.domain.ports.inbound.CompleteUseCase;
import com.argus.llmgateway.domain.ports.outbound.LlmProvider;
import com.argus.llmgateway.domain.ports.outbound.SecretScanner;
import com.argus.llmgateway.domain.ports.outbound.UsageStore;
import com.argus.llmgateway.domain.services.ModelRouter;
import com.argus.llmgateway.domain.services.StructuredOutputParser;
import com.argus.llmgateway.domain.valueobjects.Budget;
import com.argus.llmgateway.domain.valueobjects.LlmResponse;
import com.argus.llmgateway.domain.valueobjects.ModelProfile;
import com.argus.llmgateway.domain.valueobjects.StructuredSpec;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LlmGatewayService implements CompleteUseCase, CompleteStructuredUseCase {

    private final ModelRouter router;
    private final SecretScanner scanner;
    private final LlmProvider provider;
    private final UsageStore usageStore;
    private final StructuredOutputParser parser;
    private final Budget defaultBudget;
    private final Map<UUID, RunBudget> budgets = new ConcurrentHashMap<>();

    public LlmGatewayService(ModelRouter router,
                             SecretScanner scanner,
                             LlmProvider provider,
                             UsageStore usageStore,
                             StructuredOutputParser parser,
                             Budget defaultBudget) {
        this.router = router;
        this.scanner = scanner;
        this.provider = provider;
        this.usageStore = usageStore;
        this.parser = parser;
        this.defaultBudget = defaultBudget;
    }

    @Override
    public LlmResponse complete(CompleteCommand cmd) {
        // 1. routage + politique de confidentialité
        ModelProfile model = router.route(cmd.task(), cmd.policy());

        // 2. nettoyage des secrets
        String system = scanner.redact(cmd.systemPrompt());
        String user = scanner.redact(cmd.userPrompt());

        // 3. budget du run
        RunBudget budget = budgets.computeIfAbsent(cmd.runId(), id -> new RunBudget(id, defaultBudget));
        budget.assertNotExceeded();

        // 4. appel réel
        LlmResponse response = provider.generate(model, system, user);

        // 5. consommation
        double cost = response.usage().total() / 1000.0 * model.costPer1kTokens();
        budget.consume(response.usage(), cost);

        // 6. journal
        usageStore.save(new LlmCall(UUID.randomUUID(), cmd.runId(), response.modelName(),
                response.usage(), cost, response.duration(), Instant.now()));

        return response;
    }

    @Override
    public String completeStructured(CompleteCommand cmd, StructuredSpec spec) {
        // 7. extraction du JSON ou du bloc de code
        return parser.parse(complete(cmd).text(), spec);
    }
}