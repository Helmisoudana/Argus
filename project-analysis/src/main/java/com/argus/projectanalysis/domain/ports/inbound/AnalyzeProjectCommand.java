package com.argus.projectanalysis.domain.ports.inbound;

import com.argus.projectanalysis.domain.exceptions.InvalidAnalysisRequestError;

import java.nio.file.Path;
import java.util.UUID;

public record AnalyzeProjectCommand(UUID runId, Path projectRoot) {

    public AnalyzeProjectCommand {
        if (runId == null) {
            throw new InvalidAnalysisRequestError("runId", "ne doit pas être nul");
        }
        if (projectRoot == null) {
            throw new InvalidAnalysisRequestError("projectRoot", "ne doit pas être nul");
        }
    }
}