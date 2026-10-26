package com.argus.projectanalysis.domain.ports.inbound;

import com.argus.projectanalysis.domain.entities.SourceUnit;

import java.util.List;
import java.util.UUID;

public interface GetSourceUnitQuery {

    SourceUnit getSourceUnit(UUID runId, String qualifiedName);

    List<SourceUnit> listSourceUnits(UUID runId);
}