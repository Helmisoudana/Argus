package com.argus.projectanalysis.domain.entities;

import com.argus.projectanalysis.domain.valueobjects.DependencyEdge;
import com.argus.projectanalysis.domain.valueobjects.DependencyKind;

import java.util.List;

/** Qui injecte ou référence quoi : indispensable pour savoir quoi mocker. */
public record DependencyGraph(List<DependencyEdge> edges) {

    public DependencyGraph {
        edges = List.copyOf(edges);
    }

    /** Toutes les dépendances sortantes (internes et externes). */
    public List<DependencyEdge> dependenciesOf(String qualifiedName) {
        return edges.stream().filter(e -> e.from().equals(qualifiedName)).toList();
    }

    /** Unités du projet dont dépend l'unité. */
    public List<String> internalDependenciesOf(String qualifiedName) {
        return dependenciesOf(qualifiedName).stream()
                .filter(DependencyEdge::internal)
                .map(DependencyEdge::to)
                .distinct()
                .toList();
    }

    /** Unités du projet qui dépendent de l'unité. */
    public List<String> dependentsOf(String qualifiedName) {
        return edges.stream()
                .filter(e -> e.internal() && e.to().equals(qualifiedName))
                .map(DependencyEdge::from)
                .distinct()
                .toList();
    }

    public int fanOut(String qualifiedName) {
        return internalDependenciesOf(qualifiedName).size();
    }

    public int fanIn(String qualifiedName) {
        return dependentsOf(qualifiedName).size();
    }

    /** Types injectés (constructeur ou champ) : les candidats au mock. */
    public List<String> mockCandidates(String qualifiedName) {
        return dependenciesOf(qualifiedName).stream()
                .filter(e -> e.kind() == DependencyKind.CONSTRUCTOR || e.kind() == DependencyKind.FIELD)
                .map(DependencyEdge::to)
                .distinct()
                .toList();
    }
}