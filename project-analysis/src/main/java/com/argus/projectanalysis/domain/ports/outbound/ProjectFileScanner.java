package com.argus.projectanalysis.domain.ports.outbound;

import com.argus.projectanalysis.domain.valueobjects.ProjectFiles;

import java.nio.file.Path;

public interface ProjectFileScanner {
    /** Liste les fichiers Java de production (src/main/java) et de test (src/test/java). */
    ProjectFiles scan(Path projectRoot);
}