package com.argus.shared.id;

import com.argus.shared.error.Guard;
import com.argus.shared.error.InvalidInputException;
import java.util.UUID;

/** Identifiant d'un projet cible analysé. */
public record ProjectId(UUID value) implements Identifier<UUID> {

    public ProjectId {
        Guard.notNull(value, "ProjectId");
    }

    public static ProjectId newId() {
        return new ProjectId(UUID.randomUUID());
    }

    /** Lit un identifiant depuis son texte canonique (UUID). */
    public static ProjectId of(String text) {
        Guard.notBlank(text, "ProjectId");
        try {
            return new ProjectId(UUID.fromString(text.trim()));
        } catch (IllegalArgumentException e) {
            throw InvalidInputException.forField("ProjectId", "UUID invalide : \"" + text + "\"");
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
