package com.argus.llmgateway.infrastructure.module;

import com.argus.llmgateway.infrastructure.container.LlmGatewayConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

// Point d'installation du module : bootstrap importe cette classe.
@Configuration
@Import(LlmGatewayConfiguration.class)
public class LlmGatewayModule {
}