package com.argus.projectanalysis.infrastructure.module;

import com.argus.projectanalysis.infrastructure.container.ProjectAnalysisConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

// Point d'installation du module : bootstrap importe cette classe.
@Configuration
@Import(ProjectAnalysisConfiguration.class)
public class ProjectAnalysisModule {
}