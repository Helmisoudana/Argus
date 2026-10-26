package com.argus.projectanalysis.domain.ports.inbound;

import com.argus.projectanalysis.domain.entities.DependencyGraph;

import java.util.UUID;

public interface GetDependencyGraphQuery {
    DependencyGraph getDependencyGraph(UUID runId);
}