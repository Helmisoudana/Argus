package com.argus.shared.error;

/**
 * Un système externe a échoué : LLM, Git, Docker, outil de build, base de données...
 *
 * <p>Levée par les adapters pour traduire les erreurs techniques en erreur métier, afin que
 * le domaine et l'application ne voient jamais d'exception propre à une bibliothèque.
 */
public class ExternalSystemException extends argusException {

    private static final long serialVersionUID = 1L;

    private final String system;

    public ExternalSystemException(String system, String message) {
        super(ErrorCode.EXTERNAL_SYSTEM, "[%s] %s".formatted(system, message));
        this.system = system;
    }

    public ExternalSystemException(String system, String message, Throwable cause) {
        super(ErrorCode.EXTERNAL_SYSTEM, "[%s] %s".formatted(system, message), cause);
        this.system = system;
    }

    /** Nom du système en cause, ex. {@code "ollama"}, {@code "git"}, {@code "docker"}. */
    public String system() {
        return system;
    }
}
