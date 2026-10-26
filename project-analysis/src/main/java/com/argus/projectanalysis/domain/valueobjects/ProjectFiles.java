package com.argus.projectanalysis.domain.valueobjects;

import java.nio.file.Path;
import java.util.List;

/** Chemins relatifs à la racine du projet. */
public record ProjectFiles(List<Path> mainFiles, List<Path> testFiles) {

    public ProjectFiles {
        mainFiles = List.copyOf(mainFiles);
        testFiles = List.copyOf(testFiles);
    }
}