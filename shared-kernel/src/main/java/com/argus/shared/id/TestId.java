package com.argus.shared.id;

import com.argus.shared.error.Guard;
import com.argus.shared.error.InvalidInputException;
import java.util.UUID;

/** Identifiant d'un test généré. */
public record TestId(UUID value) implements Identifier<UUID> {

    public TestId {
        Guard.notNull(value, "TestId");
    }

    public static TestId newId() {
        return new TestId(UUID.randomUUID());
    }

    /** Lit un identifiant depuis son texte canonique (UUID). */
    public static TestId of(String text) {
        Guard.notBlank(text, "TestId");
        try {
            return new TestId(UUID.fromString(text.trim()));
        } catch (IllegalArgumentException e) {
            throw InvalidInputException.forField("TestId", "UUID invalide : \"" + text + "\"");
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
