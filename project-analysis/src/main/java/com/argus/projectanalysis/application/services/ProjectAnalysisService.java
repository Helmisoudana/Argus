package com.argus.projectanalysis.application.services;

import com.argus.projectanalysis.domain.entities.DependencyGraph;
import com.argus.projectanalysis.domain.entities.ExistingTest;
import com.argus.projectanalysis.domain.entities.ProjectAnalysis;
import com.argus.projectanalysis.domain.entities.SourceUnit;
import com.argus.projectanalysis.domain.events.ProjectAnalyzed;
import com.argus.projectanalysis.domain.exceptions.AnalysisNotFoundError;
import com.argus.projectanalysis.domain.exceptions.NoJavaSourceError;
import com.argus.projectanalysis.domain.exceptions.SourceUnitNotFoundError;
import com.argus.projectanalysis.domain.ports.inbound.AnalyzeProjectCommand;
import com.argus.projectanalysis.domain.ports.inbound.AnalyzeProjectUseCase;
import com.argus.projectanalysis.domain.ports.inbound.GetDependencyGraphQuery;
import com.argus.projectanalysis.domain.ports.inbound.GetSourceUnitQuery;
import com.argus.projectanalysis.domain.ports.outbound.AnalysisEventPublisher;
import com.argus.projectanalysis.domain.ports.outbound.AnalysisRepository;
import com.argus.projectanalysis.domain.ports.outbound.ProjectFileScanner;
import com.argus.projectanalysis.domain.ports.outbound.SourceCodeReader;
import com.argus.projectanalysis.domain.services.DependencyGraphBuilder;
import com.argus.projectanalysis.domain.services.ExistingTestMatcher;
import com.argus.projectanalysis.domain.valueobjects.ParseIssue;
import com.argus.projectanalysis.domain.valueobjects.ProjectFiles;
import com.argus.projectanalysis.domain.valueobjects.ReadResult;

import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ProjectAnalysisService implements AnalyzeProjectUseCase, GetSourceUnitQuery, GetDependencyGraphQuery {

    private final ProjectFileScanner scanner;
    private final SourceCodeReader reader;
    private final AnalysisRepository repository;
    private final AnalysisEventPublisher publisher;
    private final DependencyGraphBuilder graphBuilder = new DependencyGraphBuilder();
    private final ExistingTestMatcher testMatcher = new ExistingTestMatcher();

    public ProjectAnalysisService(ProjectFileScanner scanner,
                                  SourceCodeReader reader,
                                  AnalysisRepository repository,
                                  AnalysisEventPublisher publisher) {
        this.scanner = scanner;
        this.reader = reader;
        this.repository = repository;
        this.publisher = publisher;
    }

    @Override
    public ProjectAnalysis analyze(AnalyzeProjectCommand command) {
        Path root = command.projectRoot().toAbsolutePath().normalize();

        ProjectFiles files = scanner.scan(root);
        if (files.mainFiles().isEmpty()) {
            throw new NoJavaSourceError(root.toString());
        }

        ReadResult<SourceUnit> sources = reader.readSources(root, files.mainFiles());
        ReadResult<ExistingTest> tests = reader.readTests(root, files.testFiles());

        Map<String, SourceUnit> units = new LinkedHashMap<>();
        sources.items().forEach(u -> units.put(u.qualifiedName(), u));

        DependencyGraph graph = graphBuilder.build(units.values());
        Map<String, List<ExistingTest>> testsByTarget = testMatcher.match(units.values(), tests.items());

        List<ParseIssue> issues = new ArrayList<>(sources.issues());
        issues.addAll(tests.issues());

        ProjectAnalysis analysis = new ProjectAnalysis(command.runId(), root, Instant.now(), units, graph,
                tests.items(), testsByTarget, issues);

        repository.save(analysis);
        publisher.publish(new ProjectAnalyzed(UUID.randomUUID(), command.runId(), Instant.now(),
                analysis.summary()));
        return analysis;
    }

    @Override
    public SourceUnit getSourceUnit(UUID runId, String qualifiedName) {
        return analysisOf(runId).unit(qualifiedName)
                .orElseThrow(() -> new SourceUnitNotFoundError(qualifiedName));
    }

    @Override
    public List<SourceUnit> listSourceUnits(UUID runId) {
        return List.copyOf(analysisOf(runId).units().values());
    }

    @Override
    public DependencyGraph getDependencyGraph(UUID runId) {
        return analysisOf(runId).graph();
    }

    private ProjectAnalysis analysisOf(UUID runId) {
        return repository.findByRunId(runId).orElseThrow(() -> new AnalysisNotFoundError(runId));
    }
}