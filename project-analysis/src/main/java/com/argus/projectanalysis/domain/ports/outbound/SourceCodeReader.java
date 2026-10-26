package com.argus.projectanalysis.domain.ports.outbound;

import com.argus.projectanalysis.domain.entities.ExistingTest;
import com.argus.projectanalysis.domain.entities.SourceUnit;
import com.argus.projectanalysis.domain.valueobjects.ReadResult;

import java.nio.file.Path;
import java.util.Collection;

public interface SourceCodeReader {

    /** Analyse les fichiers de production (chemins relatifs à projectRoot). */
    ReadResult<SourceUnit> readSources(Path projectRoot, Collection<Path> files);

    /** Analyse les fichiers de test existants (chemins relatifs à projectRoot). */
    ReadResult<ExistingTest> readTests(Path projectRoot, Collection<Path> files);
}