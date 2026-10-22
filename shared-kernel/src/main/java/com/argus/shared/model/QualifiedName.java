package com.argus.shared.model;

import com.argus.shared.error.Guard;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Nom Java complet d'un type, ex. {@code com.acme.order.OrderService}.
 * Segments séparés par des points ; chaque segment est un identifiant Java valide.
 */
public record QualifiedName(String value) {

    private static final Pattern FORMAT = Pattern.compile(
            "^[\\p{L}_$][\\p{L}\\p{N}_$]*(\\.[\\p{L}_$][\\p{L}\\p{N}_$]*)*$");

    public QualifiedName {
        Guard.matches(value, FORMAT, "QualifiedName", "nom Java complet, ex. com.acme.OrderService");
    }

    public static QualifiedName of(String value) {
        return new QualifiedName(value);
    }

    /** Dernier segment : {@code OrderService}. */
    public String simpleName() {
        return value.substring(value.lastIndexOf('.') + 1);
    }

    /** Package : {@code com.acme.order}, ou vide pour le package par défaut. */
    public Optional<String> packageName() {
        int i = value.lastIndexOf('.');
        return i < 0 ? Optional.empty() : Optional.of(value.substring(0, i));
    }

    @Override
    public String toString() {
        return value;
    }
}
