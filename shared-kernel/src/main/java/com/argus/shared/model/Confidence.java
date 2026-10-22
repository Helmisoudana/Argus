package com.argus.shared.model;

import com.argus.shared.error.Guard;

/** Niveau de confiance borné à [0, 1] (classification d'un échec, détection d'un bug probable). */
public record Confidence(double value) {

    public static final Confidence NONE = new Confidence(0);
    public static final Confidence CERTAIN = new Confidence(1);

    public Confidence {
        Guard.inRange(value, 0, 1, "Confidence");
    }

    public static Confidence of(double value) {
        return new Confidence(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
