package com.argus.shared.event;

import com.argus.shared.error.Guard;
import com.argus.shared.id.RunId;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

/** Métadonnées obligatoires de tout événement : identité, run concerné, instant d'émission. */
public record EventMetadata(UUID eventId, RunId runId, Instant occurredAt) {

    public EventMetadata {
        Guard.notNull(eventId, "eventId");
        Guard.notNull(runId, "runId");
        Guard.notNull(occurredAt, "occurredAt");
    }

    /** Nouvelle métadonnée à l'instant courant. */
    public static EventMetadata now(RunId runId) {
        return new EventMetadata(UUID.randomUUID(), runId, Instant.now());
    }

    /** Variante avec horloge injectée, pour des tests déterministes. */
    public static EventMetadata at(RunId runId, Clock clock) {
        Guard.notNull(clock, "clock");
        return new EventMetadata(UUID.randomUUID(), runId, clock.instant());
    }
}
