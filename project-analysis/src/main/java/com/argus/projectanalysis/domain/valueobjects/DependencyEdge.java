package com.argus.projectanalysis.domain.valueobjects;

public record DependencyEdge(String from, String to, DependencyKind kind, boolean internal) { }