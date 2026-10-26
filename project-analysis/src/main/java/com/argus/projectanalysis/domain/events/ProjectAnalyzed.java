package com.argus.projectanalysis.domain.events;

import com.argus.projectanalysis.domain.valueobjects.AnalysisSummary;

import java.time.Instant;
import java.util.UUID;

/** Consommé par api-discovery et test-planning. Tout événement porte le runId. */
public record ProjectAnalyzed(UUID eventId, UUID runId, Instant occurredAt, AnalysisSummary summary) { }