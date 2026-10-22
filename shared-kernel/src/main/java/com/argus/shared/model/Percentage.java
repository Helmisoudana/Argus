package com.argus.shared.model;

import com.argus.shared.error.Guard;

/** Pourcentage borné à [0, 100] (couverture, mutation score...). */
public record Percentage(double value) {

    public static final Percentage ZERO = new Percentage(0);
    public static final Percentage FULL = new Percentage(100);

    public Percentage {
        Guard.inRange(value, 0, 100, "Percentage");
    }

    public static Percentage of(double value) {
        return new Percentage(value);
    }

    @Override
    public String toString() {
        return value + " %";
    }
}
