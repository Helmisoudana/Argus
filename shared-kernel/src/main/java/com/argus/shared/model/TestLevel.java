package com.argus.shared.model;

/** Niveau d'un test. Fixe le périmètre de argus (E2E et non-fonctionnel sont hors périmètre). */
public enum TestLevel {
    /** Test d'une classe isolée (JUnit 5, Mockito, AssertJ). */
    UNIT,
    /** Test entre couches : service, repository, base de données (Spring Boot Test, Testcontainers). */
    INTEGRATION,
    /** Test d'un scénario sur les endpoints HTTP (REST Assured). Remplace Postman. */
    API,
    /** Vérification que l'API respecte son contrat OpenAPI. */
    CONTRACT
}
