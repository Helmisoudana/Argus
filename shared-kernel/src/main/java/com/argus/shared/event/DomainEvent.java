package com.argus.shared.event;

import com.argus.shared.id.RunId;
import java.time.Instant;
import java.util.UUID;

/**
 * Contrat de tout événement de domaine. Chaque événement est un record qui porte un
 * {@link EventMetadata} ; les trois accesseurs usuels sont fournis par défaut.
 *
 * <pre>{@code
 * public record TestsExecuted(EventMetadata metadata, int passed, int failed) implements DomainEvent { }
 * }</pre>
 */
public interface DomainEvent {

    EventMetadata metadata();

    default UUID eventId() {
        return metadata().eventId();
    }

    /** Tout événement est rattaché à un run. */
    default RunId runId() {
        return metadata().runId();
    }

    default Instant occurredAt() {
        return metadata().occurredAt();
    }

    /** Nom du type d'événement, utile aux logs et à l'audit. */
    default String eventType() {
        return getClass().getSimpleName();
    }
}
