package com.argus.projectanalysis.domain.valueobjects;

/**
 * cyclomatic = 1 + points de décision ; branches = 2 x points de décision
 * (if, boucles, ternaire, case, catch, && et ||). Une méthode sans corps vaut 0.
 */
public record ComplexityMetrics(int cyclomatic, int branches, int linesOfCode) {

    public static final ComplexityMetrics ZERO = new ComplexityMetrics(0, 0, 0);

    public ComplexityMetrics plus(ComplexityMetrics other) {
        return new ComplexityMetrics(cyclomatic + other.cyclomatic,
                branches + other.branches,
                linesOfCode + other.linesOfCode);
    }
}