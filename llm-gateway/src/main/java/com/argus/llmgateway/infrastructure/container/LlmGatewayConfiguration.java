package com.argus.llmgateway.infrastructure.container;

import com.argus.llmgateway.application.services.LlmGatewayService;
import com.argus.llmgateway.domain.ports.outbound.LlmProvider;
import com.argus.llmgateway.domain.ports.outbound.SecretScanner;
import com.argus.llmgateway.domain.ports.outbound.UsageStore;
import com.argus.llmgateway.domain.services.ModelRouter;
import com.argus.llmgateway.domain.services.StructuredOutputParser;
import com.argus.llmgateway.domain.valueobjects.Budget;
import com.argus.llmgateway.domain.valueobjects.ModelProfile;
import com.argus.llmgateway.domain.valueobjects.TaskType;
import com.argus.llmgateway.infrastructure.adapters.outbound.ollama.OllamaLlmProvider;
import com.argus.llmgateway.infrastructure.adapters.outbound.secrets.RegexSecretScanner;
import com.argus.llmgateway.infrastructure.adapters.outbound.usage.InMemoryUsageStore;
import com.argus.llmgateway.infrastructure.config.LlmGatewayProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.util.EnumMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties(LlmGatewayProperties.class)
public class LlmGatewayConfiguration {

    @Bean
    ModelRouter modelRouter(LlmGatewayProperties props) {
        Map<TaskType, ModelProfile> catalogue = new EnumMap<>(TaskType.class);
        props.ollama().models().forEach((task, modelName) ->
                catalogue.put(task, new ModelProfile("ollama", modelName, true, 8192, 0.0)));
        return new ModelRouter(catalogue);
    }

    @Bean
    LlmProvider llmProvider(LlmGatewayProperties props) {
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(props.ollama().timeout());
        return new OllamaLlmProvider(RestClient.builder()
                .baseUrl(props.ollama().baseUrl())
                .requestFactory(factory)
                .build());
    }

    @Bean
    SecretScanner secretScanner() {
        return new RegexSecretScanner();
    }

    @Bean
    UsageStore usageStore() {
        return new InMemoryUsageStore();
    }

    @Bean
    StructuredOutputParser structuredOutputParser() {
        return new StructuredOutputParser();
    }

    @Bean
    LlmGatewayService llmGatewayService(ModelRouter router,
                                        SecretScanner scanner,
                                        LlmProvider provider,
                                        UsageStore usageStore,
                                        StructuredOutputParser parser,
                                        LlmGatewayProperties props) {
        Budget budget = new Budget(props.runBudget().maxTokens(), props.runBudget().maxCost());
        return new LlmGatewayService(router, scanner, provider, usageStore, parser, budget);
    }
}