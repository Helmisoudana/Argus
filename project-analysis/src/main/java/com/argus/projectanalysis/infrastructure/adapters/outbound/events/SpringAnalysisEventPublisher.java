package com.argus.projectanalysis.infrastructure.adapters.outbound.events;

import com.argus.projectanalysis.domain.events.ProjectAnalyzed;
import com.argus.projectanalysis.domain.ports.outbound.AnalysisEventPublisher;
import org.springframework.context.ApplicationEventPublisher;

public class SpringAnalysisEventPublisher implements AnalysisEventPublisher {

    private final ApplicationEventPublisher publisher;

    public SpringAnalysisEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void publish(ProjectAnalyzed event) {
        publisher.publishEvent(event);
    }
}