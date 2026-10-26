package com.argus.projectanalysis.domain.ports.outbound;

import com.argus.projectanalysis.domain.entities.ProjectAnalysis;

import java.util.Optional;
import java.util.UUID;

public interface AnalysisRepository {

    void save(ProjectAnalysis analysis);

    Optional<ProjectAnalysis> findByRunId(UUID runId);
}