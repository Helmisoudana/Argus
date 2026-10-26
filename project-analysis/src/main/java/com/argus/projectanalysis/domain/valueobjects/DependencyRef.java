package com.argus.projectanalysis.domain.valueobjects;

/** Type référencé par une unité (nom complet quand un import le permet). */
public record DependencyRef(String type, DependencyKind kind) { }