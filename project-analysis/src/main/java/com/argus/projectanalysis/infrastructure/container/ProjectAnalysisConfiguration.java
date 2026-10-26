package com.argus.projectanalysis.infrastructure.container;

import com.argus.projectanalysis.application.services.ProjectAnalysisService;
import com.argus.projectanalysis.domain.ports.outbound.AnalysisEventPublisher;
import com.argus.projectanalysis.domain.ports.outbound.AnalysisRepository;
import com.argus.projectanalysis.domain.ports.outbound.ProjectFileScanner;
import com.argus.projectanalysis.domain.ports.outbound.SourceCodeReader;
import com.argus.projectanalysis.infrastructure.adapters.outbound.events.SpringAnalysisEventPublisher;
import com.argus.projectanalysis.infrastructure.adapters.outbound.filesystem.FileSystemProjectFileScanner;
import com.argus.projectanalysis.infrastructure.adapters.outbound.javaparser.JavaParserSourceCodeReader;
import com.argus.projectanalysis.infrastructure.adapters.outbound.persistence.InMemoryAnalysisRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProjectAnalysisConfiguration {

    @Bean
    ProjectFileScanner projectFileScanner() {
        return new FileSystemProjectFileScanner();
    }

    @Bean
    SourceCodeReader sourceCodeReader() {
        return new JavaParserSourceCodeReader();
    }

    @Bean
    AnalysisRepository analysisRepository() {
        return new InMemoryAnalysisRepository();
    }

    @Bean
    AnalysisEventPublisher analysisEventPublisher(ApplicationEventPublisher publisher) {
        return new SpringAnalysisEventPublisher(publisher);
    }

    @Bean
    ProjectAnalysisService projectAnalysisService(ProjectFileScanner scanner,
                                                  SourceCodeReader reader,
                                                  AnalysisRepository repository,
                                                  AnalysisEventPublisher publisher) {
        return new ProjectAnalysisService(scanner, reader, repository, publisher);
    }
}