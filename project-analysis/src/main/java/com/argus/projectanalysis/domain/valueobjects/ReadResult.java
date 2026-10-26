package com.argus.projectanalysis.domain.valueobjects;

import java.util.List;

public record ReadResult<T>(List<T> items, List<ParseIssue> issues) {

    public ReadResult {
        items = List.copyOf(items);
        issues = List.copyOf(issues);
    }
}