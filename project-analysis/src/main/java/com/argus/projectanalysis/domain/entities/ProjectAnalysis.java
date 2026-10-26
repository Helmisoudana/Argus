package com.argus.projectanalysis.domain.entities;

import com.argus.projectanalysis.domain.valueobjects.AnalysisSummary;
import com.argus.projectanalysis.domain.valueobjects.ParseIssue;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public record ProjectAnalysis(UUID runId,
                              Path projectRoot,
                              Instant analyzedAt,
                              Map<String, SourceUnit> units,
                              DependencyGraph graph,
                              List<ExistingTest> existingTests,
                              Map<String, List<ExistingTest>> testsByTarget,
                              List<ParseIssue> parseIssues) {

    public ProjectAnalysis {
        units = Collections.unmodifiableMap(new LinkedHashMap<>(units));
        existingTests = List.copyOf(existingTests);
        testsByTarget = Collections.unmodifiableMap(new LinkedHashMap<>(testsByTarget));
        parseIssues = List.copyOf(parseIssues);
    }

    public Optional<SourceUnit> unit(String qualifiedName) {
        return Optional.ofNullable(units.get(qualifiedName));
    }

    public List<ExistingTest> testsFor(String qualifiedName) {
        return testsByTarget.getOrDefault(qualifiedName, List.of());
    }

    /** Unités testables sans aucune classe de test associée. */
    public List<SourceUnit> untestedUnits() {
        return units.values().stream()
                .filter(u -> u.isTestable() && testsFor(u.qualifiedName()).isEmpty())
                .toList();
    }

    public AnalysisSummary summary() {
        int methods = units.values().stream().mapToInt(u -> u.methods().size()).sum();
        int cyclomatic = units.values().stream().mapToInt(u -> u.metrics().cyclomatic()).sum();
        return new AnalysisSummary(units.size(), methods, cyclomatic, untestedUnits().size(),
                existingTests.size(), parseIssues.size());
    }
}