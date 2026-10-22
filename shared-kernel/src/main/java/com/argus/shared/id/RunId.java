package com.argus.shared.id;

import com.argus.shared.error.Guard;
import com.argus.shared.error.InvalidInputException;
import java.util.UUID;

/** Identifiant d'un run (une exécution complète de l'agent). */
public record RunId(UUID value) implements Identifier<UUID> {

    public RunId {
        Guard.notNull(value, "RunId");
    }

    public static RunId newId() {
        return new RunId(UUID.randomUUID());
    }

    /** Lit un identifiant depuis son texte canonique (UUID). */
    public static RunId of(String text) {
        Guard.notBlank(text, "RunId");
        try {
            return new RunId(UUID.fromString(text.trim()));
        } catch (IllegalArgumentException e) {
            throw InvalidInputException.forField("RunId", "UUID invalide : \"" + text + "\"");
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
