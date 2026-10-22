package com.argus.shared.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.argus.shared.error.InvalidInputException;
import com.argus.shared.id.RunId;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class DomainEventTest {

    /** Événement factice : montre la forme attendue d'un événement de module. */
    record SampleEvent(EventMetadata metadata, String payload) implements DomainEvent { }

    private final RunId runId = RunId.newId();
    private final Clock clock = Clock.fixed(Instant.parse("2026-10-22T10:00:00Z"), ZoneOffset.UTC);

    @Test
    void default_accessors_delegate_to_metadata() {
        SampleEvent event = new SampleEvent(EventMetadata.at(runId, clock), "x");
        assertThat(event.runId()).isEqualTo(runId);
        assertThat(event.occurredAt()).isEqualTo(Instant.parse("2026-10-22T10:00:00Z"));
        assertThat(event.eventId()).isNotNull();
        assertThat(event.eventType()).isEqualTo("SampleEvent");
    }

    @Test
    void now_generates_a_fresh_event_id_each_time() {
        assertThat(EventMetadata.now(runId).eventId()).isNotEqualTo(EventMetadata.now(runId).eventId());
    }

    @Test
    void metadata_rejects_missing_values() {
        assertThatThrownBy(() -> new EventMetadata(null, runId, Instant.now())).isInstanceOf(InvalidInputException.class);
        assertThatThrownBy(() -> new EventMetadata(UUID.randomUUID(), null, Instant.now()))
                .isInstanceOf(InvalidInputException.class);
        assertThatThrownBy(() -> new EventMetadata(UUID.randomUUID(), runId, null))
                .isInstanceOf(InvalidInputException.class);
        assertThatThrownBy(() -> EventMetadata.at(runId, null)).isInstanceOf(InvalidInputException.class);
    }
}
