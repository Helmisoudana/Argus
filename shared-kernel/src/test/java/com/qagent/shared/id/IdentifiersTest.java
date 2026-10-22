package com.argus.shared.id;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.argus.shared.error.InvalidInputException;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class IdentifiersTest {

    @Test
    void newId_generates_distinct_values() {
        assertThat(RunId.newId()).isNotEqualTo(RunId.newId());
    }

    @Test
    void of_reads_the_canonical_text_form() {
        RunId id = RunId.newId();
        assertThat(RunId.of(id.toString())).isEqualTo(id);
        assertThat(ProjectId.of(" " + id + " ").value()).isEqualTo(id.value());
    }

    @Test
    void of_rejects_invalid_text() {
        assertThatThrownBy(() -> RunId.of("pas-un-uuid")).isInstanceOf(InvalidInputException.class);
        assertThatThrownBy(() -> TestId.of(" ")).isInstanceOf(InvalidInputException.class);
    }

    @Test
    void constructor_rejects_null() {
        assertThatThrownBy(() -> new TargetId(null)).isInstanceOf(InvalidInputException.class);
    }

    @Test
    void same_uuid_in_different_id_types_are_different_types() {
        UUID uuid = UUID.randomUUID();
        Object run = new RunId(uuid);
        Object test = new TestId(uuid);
        assertThat(run).isNotEqualTo(test);
    }

    @Test
    void ids_implement_the_Identifier_contract() {
        UUID uuid = UUID.randomUUID();
        Identifier<UUID> id = new ProjectId(uuid);
        assertThat(id.value()).isEqualTo(uuid);
    }
}
