package com.argus.projectanalysis.domain.exceptions;

public final class InvalidAnalysisRequestError extends ProjectAnalysisError {

    private final String field;

    public InvalidAnalysisRequestError(String field, String reason) {
        super(ProjectAnalysisErrorCode.INVALID_INPUT, field + " : " + reason);
        this.field = field;
    }

    public String field() {
        return field;
    }
}