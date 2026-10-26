package com.argus.projectanalysis.domain.valueobjects;

public record AnalysisSummary(int totalUnits,
                              int totalMethods,
                              int totalCyclomaticComplexity,
                              int untestedUnits,
                              int existingTestClasses,
                              int parseIssues) { }