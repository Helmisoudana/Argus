package com.argus.llmgateway.domain.ports.outbound;

public interface SecretScanner {
    /** Masque les secrets (clés, tokens, mots de passe) avant tout envoi au LLM. */
    String redact(String text);
}