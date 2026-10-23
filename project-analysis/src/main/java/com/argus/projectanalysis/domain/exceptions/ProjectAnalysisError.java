package com.argus.projectanalysis.domain.exceptions;

public abstract class ProjectAnalysisError extends RuntimeException {

    private final ProjectAnalysisErrorCode code;

    protected ProjectAnalysisError(ProjectAnalysisErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    public ProjectAnalysisErrorCode code() {
        return code;
    }
}