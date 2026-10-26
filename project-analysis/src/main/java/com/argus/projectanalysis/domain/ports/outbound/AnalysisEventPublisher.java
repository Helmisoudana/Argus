package com.argus.projectanalysis.domain.ports.outbound;

import com.argus.projectanalysis.domain.events.ProjectAnalyzed;

public interface AnalysisEventPublisher {
    void publish(ProjectAnalyzed event);
}