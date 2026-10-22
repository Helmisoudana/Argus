package com.argus.llmgateway.domain.ports.inbound;

import com.argus.llmgateway.domain.valueobjects.StructuredSpec;

public interface CompleteStructuredUseCase {
    /** Retourne uniquement la partie utile (JSON ou code) de la réponse. */
    String completeStructured(CompleteCommand command, StructuredSpec spec);
}