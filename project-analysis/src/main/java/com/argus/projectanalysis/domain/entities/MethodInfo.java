package com.argus.projectanalysis.domain.entities;

import com.argus.projectanalysis.domain.valueobjects.CodeLocation;
import com.argus.projectanalysis.domain.valueobjects.ComplexityMetrics;
import com.argus.projectanalysis.domain.valueobjects.ParameterInfo;
import com.argus.projectanalysis.domain.valueobjects.Visibility;

import java.util.List;

public record MethodInfo(String name,
                         String signature,
                         String returnType,
                         Visibility visibility,
                         List<ParameterInfo> parameters,
                         List<String> thrownTypes,
                         boolean isStatic,
                         boolean isAbstract,
                         CodeLocation location,
                         ComplexityMetrics metrics) {

    public MethodInfo {
        parameters = List.copyOf(parameters);
        thrownTypes = List.copyOf(thrownTypes);
    }

    /** Une méthode privée ou abstraite n'est pas une cible directe de test. */
    public boolean isTestable() {
        return visibility != Visibility.PRIVATE && !isAbstract;
    }
}