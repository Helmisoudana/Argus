package com.argus.shared.id;

import com.argus.shared.error.Guard;
import com.argus.shared.error.InvalidInputException;
import java.util.UUID;

/** Identifiant d'une cible de test (classe, méthode ou endpoint) dans un plan. */
public record TargetId(UUID value) implements Identifier<UUID> {

    public TargetId {
        Guard.notNull(value, "TargetId");
    }

    public static TargetId newId() {
        return new TargetId(UUID.randomUUID());
    }

    /** Lit un identifiant depuis son texte canonique (UUID). */
    public static TargetId of(String text) {
        Guard.notBlank(text, "TargetId");
        try {
            return new TargetId(UUID.fromString(text.trim()));
        } catch (IllegalArgumentException e) {
            throw InvalidInputException.forField("TargetId", "UUID invalide : \"" + text + "\"");
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
