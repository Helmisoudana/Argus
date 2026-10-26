package com.argus.projectanalysis.infrastructure.adapters.outbound.persistence;

import com.argus.projectanalysis.domain.entities.ProjectAnalysis;
import com.argus.projectanalysis.domain.ports.outbound.AnalysisRepository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryAnalysisRepository implements AnalysisRepository {

    private final Map<UUID, ProjectAnalysis> analyses = new ConcurrentHashMap<>();

    @Override
    public void save(ProjectAnalysis analysis) {
        analyses.put(analysis.runId(), analysis);
    }

    @Override
    public Optional<ProjectAnalysis> findByRunId(UUID runId) {
        return Optional.ofNullable(analyses.get(runId));
    }
}