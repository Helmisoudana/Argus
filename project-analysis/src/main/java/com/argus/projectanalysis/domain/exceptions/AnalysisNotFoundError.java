package com.argus.projectanalysis.domain.exceptions;

import java.util.UUID;

public final class AnalysisNotFoundError extends ProjectAnalysisError {

    public AnalysisNotFoundError(UUID runId) {
        super(ProjectAnalysisErrorCode.ANALYSIS_NOT_FOUND, "Aucune analyse pour le run " + runId);
    }
}