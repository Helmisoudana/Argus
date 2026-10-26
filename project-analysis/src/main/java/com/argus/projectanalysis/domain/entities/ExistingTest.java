package com.argus.projectanalysis.domain.entities;

import com.argus.projectanalysis.domain.valueobjects.CodeLocation;

/** Classe de test déjà présente dans le projet cible. */
public record ExistingTest(String qualifiedName,
                           String packageName,
                           String simpleName,
                           int testMethodCount,
                           CodeLocation location) { }