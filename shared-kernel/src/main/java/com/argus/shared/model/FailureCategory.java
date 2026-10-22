package com.argus.shared.model;

/** Résultat de la classification d'un échec par {@code failure-analysis}. */
public enum FailureCategory {
    /** Le test est faux (compilation, mock, assertion mal formée) : à réparer. */
    TEST_ERROR,
    /** Instabilité d'environnement ou test flaky : relance ou quarantaine. */
    ENVIRONMENT_OR_FLAKY,
    /** Le test est cohérent avec la spécification et l'application diverge : à signaler, jamais à « réparer ». */
    SUSPECTED_APP_BUG
}
