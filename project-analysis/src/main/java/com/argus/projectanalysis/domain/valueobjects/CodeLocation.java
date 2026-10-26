package com.argus.projectanalysis.domain.valueobjects;

/** Fichier relatif à la racine du projet, séparateur '/'. */
public record CodeLocation(String file, int startLine, int endLine) {

    public int lineCount() {
        return endLine - startLine + 1;
    }
}