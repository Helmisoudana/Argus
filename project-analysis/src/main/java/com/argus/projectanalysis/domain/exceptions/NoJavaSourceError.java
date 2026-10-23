package com.argus.projectanalysis.domain.exceptions;

/** La v1 n'analyse que du code Java (src/main/java). */
public final class NoJavaSourceError extends ProjectAnalysisError {

    public NoJavaSourceError(String path) {
        super(ProjectAnalysisErrorCode.NO_JAVA_SOURCE,
                "Aucun fichier Java dans src/main/java : " + path + " (seul Java est supporté)");
    }
}