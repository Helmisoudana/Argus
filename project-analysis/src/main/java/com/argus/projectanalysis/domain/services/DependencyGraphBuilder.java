package com.argus.projectanalysis.domain.services;

import com.argus.projectanalysis.domain.entities.DependencyGraph;
import com.argus.projectanalysis.domain.entities.SourceUnit;
import com.argus.projectanalysis.domain.valueobjects.DependencyEdge;
import com.argus.projectanalysis.domain.valueobjects.DependencyRef;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class DependencyGraphBuilder {

    public DependencyGraph build(Collection<SourceUnit> units) {
        Set<String> names = new LinkedHashSet<>();
        Map<String, List<String>> bySimpleName = new HashMap<>();
        for (SourceUnit unit : units) {
            names.add(unit.qualifiedName());
            bySimpleName.computeIfAbsent(unit.simpleName(), k -> new ArrayList<>()).add(unit.qualifiedName());
        }

        Set<DependencyEdge> edges = new LinkedHashSet<>();
        for (SourceUnit unit : units) {
            for (DependencyRef ref : unit.dependencies()) {
                resolve(unit, ref, names, bySimpleName).ifPresent(edges::add);
            }
        }
        return new DependencyGraph(List.copyOf(edges));
    }

    private Optional<DependencyEdge> resolve(SourceUnit from, DependencyRef ref,
                                             Set<String> names, Map<String, List<String>> bySimpleName) {
        String raw = ref.type();

        // 1. nom complet présent dans le projet
        if (names.contains(raw)) {
            return edge(from, raw, ref, true);
        }
        // 2. même package
        String samePackage = from.packageName().isEmpty() ? raw : from.packageName() + "." + raw;
        if (names.contains(samePackage)) {
            return edge(from, samePackage, ref, true);
        }
        boolean qualified = raw.contains(".") && Character.isLowerCase(raw.charAt(0));
        // 3. nom simple unique dans le projet
        if (!qualified) {
            List<String> candidates = bySimpleName.getOrDefault(raw.substring(raw.lastIndexOf('.') + 1), List.of());
            if (candidates.size() == 1) {
                return edge(from, candidates.get(0), ref, true);
            }
        }
        // 4. type externe identifiable (nom complet hors JDK)
        if (qualified && !raw.startsWith("java.")) {
            return edge(from, raw, ref, false);
        }
        // 5. nom simple non résolu (java.lang, générique...) : ignoré
        return Optional.empty();
    }

    private Optional<DependencyEdge> edge(SourceUnit from, String to, DependencyRef ref, boolean internal) {
        if (from.qualifiedName().equals(to)) {
            return Optional.empty();
        }
        return Optional.of(new DependencyEdge(from.qualifiedName(), to, ref.kind(), internal));
    }
}