package com.argus.projectanalysis.domain.entities;

import com.argus.projectanalysis.domain.valueobjects.CodeLocation;
import com.argus.projectanalysis.domain.valueobjects.ComplexityMetrics;
import com.argus.projectanalysis.domain.valueobjects.DependencyRef;
import com.argus.projectanalysis.domain.valueobjects.Stereotype;
import com.argus.projectanalysis.domain.valueobjects.UnitKind;

import java.util.List;
import java.util.Set;

/** Unité à tester : classe, interface, enum ou record Java du projet. */
public record SourceUnit(String qualifiedName,
                         String packageName,
                         String simpleName,
                         UnitKind kind,
                         Stereotype stereotype,
                         Set<String> annotations,
                         List<String> superTypes,
                         List<DependencyRef> dependencies,
                         List<MethodInfo> methods,
                         List<MethodInfo> constructors,
                         CodeLocation location,
                         ComplexityMetrics metrics) {

    public SourceUnit {
        annotations = Set.copyOf(annotations);
        superTypes = List.copyOf(superTypes);
        dependencies = List.copyOf(dependencies);
        methods = List.copyOf(methods);
        constructors = List.copyOf(constructors);
    }

    /** Vrai si l'unité contient du code exécutable à tester. */
    public boolean isTestable() {
        return kind != UnitKind.INTERFACE
                && kind != UnitKind.ANNOTATION
                && methods.stream().anyMatch(MethodInfo::isTestable);
    }

    public int maxMethodComplexity() {
        return methods.stream().mapToInt(m -> m.metrics().cyclomatic()).max().orElse(0);
    }
}