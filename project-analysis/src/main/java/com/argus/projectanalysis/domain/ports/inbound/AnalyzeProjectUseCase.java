package com.argus.projectanalysis.domain.ports.inbound;

import com.argus.projectanalysis.domain.entities.ProjectAnalysis;

public interface AnalyzeProjectUseCase {
    ProjectAnalysis analyze(AnalyzeProjectCommand command);
}