package com.argus.projectanalysis.domain.exceptions;

public final class ProjectNotFoundError extends ProjectAnalysisError {

    public ProjectNotFoundError(String path) {
        super(ProjectAnalysisErrorCode.PROJECT_NOT_FOUND, "Projet introuvable ou illisible : " + path);
    }
}