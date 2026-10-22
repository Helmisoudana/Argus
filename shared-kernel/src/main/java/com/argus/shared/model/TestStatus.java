package com.argus.shared.model;

/** Cycle de vie d'un test généré, de la planification à l'issue finale. */
public enum TestStatus {
    /** Prévu par le plan, pas encore généré. */
    PLANNED,
    /** Code de test produit, pas encore compilé. */
    GENERATED,
    /** Compile, pas encore exécuté. */
    COMPILED,
    /** Exécuté avec succès. */
    PASSED,
    /** Exécuté et en échec (analyse en cours ou signalé comme bug probable). */
    FAILED,
    /** Instable (flaky) : écarté des résultats, mais conservé et signalé. */
    QUARANTINED,
    /** Abandonné après échec de la réparation, avec raison dans le rapport. */
    DISCARDED
}
