package com.argus.projectanalysis.domain.exceptions;

public final class SourceUnitNotFoundError extends ProjectAnalysisError {

    public SourceUnitNotFoundError(String qualifiedName) {
        super(ProjectAnalysisErrorCode.SOURCE_UNIT_NOT_FOUND, "Unité de code introuvable : " + qualifiedName);
    }
}