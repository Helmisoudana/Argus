package com.argus.shared.error;

import java.util.Collection;
import java.util.regex.Pattern;

/**
 * Vérifications d'invariants, utilisées par les constructeurs compacts des types du kernel
 * (et librement par les autres modules).
 *
 * <p>Chaque méthode renvoie la valeur validée, ce qui permet d'écrire
 * {@code this.name = Guard.notBlank(name, "name");}. En cas d'échec : {@link InvalidInputException}.
 * Ce n'est PAS un « utils » généraliste : seulement des contrôles d'invariants.
 */
public final class Guard {

    private Guard() {
    }

    public static <T> T notNull(T value, String name) {
        if (value == null) {
            throw InvalidInputException.forField(name, "ne doit pas être nul");
        }
        return value;
    }

    public static String notBlank(String value, String name) {
        notNull(value, name);
        if (value.isBlank()) {
            throw InvalidInputException.forField(name, "ne doit pas être vide");
        }
        return value;
    }

    public static <C extends Collection<?>> C notEmpty(C value, String name) {
        notNull(value, name);
        if (value.isEmpty()) {
            throw InvalidInputException.forField(name, "ne doit pas être une collection vide");
        }
        return value;
    }

    /** Vérifie {@code min <= value <= max} ; {@code NaN} et l'infini sont refusés. */
    public static double inRange(double value, double min, double max, String name) {
        if (Double.isNaN(value) || Double.isInfinite(value) || value < min || value > max) {
            throw InvalidInputException.forField(name, "doit être compris entre %s et %s (reçu : %s)"
                    .formatted(min, max, value));
        }
        return value;
    }

    public static int positive(int value, String name) {
        if (value <= 0) {
            throw InvalidInputException.forField(name, "doit être strictement positif (reçu : " + value + ")");
        }
        return value;
    }

    public static int nonNegative(int value, String name) {
        if (value < 0) {
            throw InvalidInputException.forField(name, "ne doit pas être négatif (reçu : " + value + ")");
        }
        return value;
    }

    /** Vérifie que la valeur correspond au motif ; {@code expected} décrit le format attendu dans le message. */
    public static String matches(String value, Pattern pattern, String name, String expected) {
        notNull(value, name);
        if (!pattern.matcher(value).matches()) {
            throw InvalidInputException.forField(name,
                    "format invalide (attendu : %s, reçu : \"%s\")".formatted(expected, value));
        }
        return value;
    }

    /** Condition générique : lève {@link InvalidInputException} avec le message fourni si elle est fausse. */
    public static void that(boolean condition, String message) {
        if (!condition) {
            throw new InvalidInputException(message);
        }
    }
}
