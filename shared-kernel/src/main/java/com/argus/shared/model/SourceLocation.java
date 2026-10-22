package com.argus.shared.model;

import com.argus.shared.error.Guard;

/** Position dans un fichier source ou de test. Ligne et colonne commencent à 1. */
public record SourceLocation(String file, int line, int column) {

    public SourceLocation {
        Guard.notBlank(file, "file");
        Guard.positive(line, "line");
        Guard.positive(column, "column");
    }

    @Override
    public String toString() {
        return file + ":" + line + ":" + column;
    }
}
